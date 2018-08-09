# !/usr/bin/env python
__author__ = "Jiajun Chen(Carson)"

import boto3
import json
import urllib2
import urllib
import time
import re
import datetime
import requests

# STEP 1: Get credentials
with open('credentials.json','r') as file:
    """Get credentials

    Get credentials like access key and secret key from file

    """
    credentials = json.load(file)

"""
    Constants and global variables
"""
ACCESS_KEY = credentials.get('access_key')
SECRET_KEY = credentials.get('secret_key')
SUBMISSION_PWD = credentials.get('submission_pwd')
ANDREW_ID = credentials.get('andrew_id')

LG_IMAGE_ID = 'ami-7747a30d'
LG_INSTANCE_TYPE = 'm3.medium'

WS_IMAGE_ID = 'ami-247a9e5e'
WS_INSTANCE_TYPE = 'm3.medium'

SECURITY_GROUP = 'ezpass-aws'

ws_created_at = ''


def start_load_generator(ec2_resource):
    """Start load generator

    Initialize the load generator, launches a m3.medium load generator

    :param ec2_resource: ec2 resource client
    :return: instance object
    """
    print "Going to start a load generator."
    response_lg = ec2_resource.create_instances(
        ImageId=LG_IMAGE_ID,
        InstanceType=LG_INSTANCE_TYPE,
        MaxCount=1,
        MinCount=1,
        SecurityGroups=[SECURITY_GROUP],
        TagSpecifications=[
            {
                'ResourceType': 'instance',
                'Tags': [{'Key': 'Project', 'Value': '2.1'}]
            }
        ]
    )

    instance_lg = response_lg[0]

    instance_lg.wait_until_running()

    instance_lg.load()

    print instance_lg
    return instance_lg


def start_web_service(ec2_resource):
    """ start web service

    Initialize the web service, launches a m3.medium web service instance

    :param ec2_resource: ec2 resource client
    :return: instance object
    """
    print "Going to create a web service instance."
    response_ws = ec2_resource.create_instances(
        ImageId=WS_IMAGE_ID,
        InstanceType=WS_INSTANCE_TYPE,
        MaxCount=1,
        MinCount=1,
        SecurityGroups=[SECURITY_GROUP],
        TagSpecifications=[
            {
                'ResourceType': 'instance',
                'Tags': [{'Key': 'Project', 'Value': '2.1'}]
            }
        ]
    )

    instance_ws = response_ws[0]

    instance_ws.wait_until_running()

    instance_ws.load()

    global ws_created_at
    ws_created_at = datetime.datetime.now()
    print "WS created: {}".format(ws_created_at)
    return instance_ws


def start_monitor(ec2_resource, log_url):
    """ start monitor

    Initialize the monitor to add web service when needed

    :param ec2_resource: ec2 resource client
    :param log_url: url of the log file
    :return:
    """
    print "Going to start monitoring."
    global ws_created_at
    global lg_url
    print "ws_created_at: {}".format(ws_created_at)
    print type(ws_created_at)
    print "lg_url: {}".format(lg_url)
    past = ws_created_at
    # input test id
    while True:
        # print last line of a webpage
        monitor_url = 'http://' + lg_url + log_url
        print monitor_url
        while True:
            if check_url_is_up(monitor_url) is True:
                break
            else:
                time.sleep(5)
                continue

        log = urllib2.urlopen(monitor_url)
        last_line = log.readlines()[-2]
        print "Last line: {}".format(last_line)
        if 'rps' in last_line.lower():
            print "rps in the last line"
            rps = int(re.findall(r'\d+', last_line)[0])
            print rps
            print rps < 4000
            if rps < 4000:
                now = datetime.datetime.now()
                seconds = (now - past).seconds
                print (now - past).seconds
                if seconds < 100:
                    time.sleep(seconds)
                ws_ins = start_web_service(ec2_resource)
                ws_dns = ws_ins.public_dns_name
                print "Adding a new web service instance"
                helper_data = {'dns': ws_dns}
                url_values = urllib.urlencode(helper_data)
                print url_values
                helper_url = 'http://' + lg_url + '/test/horizontal/add?' + url_values
                print helper_url
                while True:
                    if check_url_is_up(helper_url) is True:
                        break
                    else:
                        time.sleep(10)
                        continue
                try:
                    result = requests.get(helper_url)
                    print result.content
                    past = datetime.datetime.now()
                    continue
                except Exception:
                    continue
            else:
                print "RPS over 4000! Mission accomplished."
                break
        else:
            print "there is no rps in the line"
            time.sleep(20)
            continue


def check_url_is_up(url):
    """check url is up

    Check whether an url is accessible

    :param url: url to be checked
    :return: {boolean}
    """
    try:
        urllib2.urlopen(url,timeout=5)
        return True
    except Exception:
        return False


# STEP 2: Initialize the load generator

ec2_resource = boto3.resource('ec2',region_name='us-east-1')
ec2_client = boto3.client('ec2',region_name='us-east-1')
lg_instance = start_load_generator(ec2_resource)
lg_url = lg_instance.public_dns_name
lg_id = lg_instance.instance_id
print "lg_id:  {}".format(lg_id)
print lg_url
ws_instance = start_web_service(ec2_resource)
ws_url = ws_instance.public_dns_name
ws_id = ws_instance.instance_id
print ws_url
print "ws_id: {}".format(ws_id)


# Check the status of the instances to ensure they are online
while True:
    try:
        des_lg = ec2_client.describe_instance_status(InstanceIds=[lg_id, ws_id])
        lg_status = des_lg["InstanceStatuses"][0]["InstanceState"]["Name"]
        ws_status = des_lg["InstanceStatuses"][1]["InstanceState"]["Name"]
        print des_lg
        print lg_status
        print ws_status
        if lg_status == 'running' and ws_status == 'running':
            print "Both are running!"
            break
        else:
            print "Instances are not ready yet."
            time.sleep(10)
            continue
    except Exception:
        print "Instances are not ready yet."
        time.sleep(10)
        continue


# STEP 4: Submit password and andrewid to the load generator
submit_data = {'passwd':SUBMISSION_PWD,'andrewid':ANDREW_ID}
submit_params = urllib.urlencode(submit_data)
print submit_params
url = 'http://'+lg_url + '/password?' + submit_params
print url
try:
    try:
        while True:
            if check_url_is_up(url) is True:
                break
            else:
                time.sleep(5)
                continue
        submit_result = requests.get(url)
        print submit_result.content
        print submit_result.status_code
        if submit_result.status_code == '400':
            print "Authentication has already been done."
        else:
            print "Authentication success."
            # STEP 5: Submit web service VM's DNS name to the load generator.
        test_data = {'dns': ws_url}
        test_params = urllib.urlencode(test_data)
        print test_params
        test_url = 'http://' + lg_url + '/test/horizontal?' + test_params
        print test_url
        while True:
            if check_url_is_up(test_url) is True:
                break
            else:
                time.sleep(5)
                continue
        # test_result = urllib2.urlopen(test_url)
        test_result = requests.get(test_url)
        test_page = test_result.content
        # test_page = test_result.read()
        print test_page
        log_url = re.search('<a +href=\'(.+?)\' *>', test_page).group()[9:-2]
        print log_url

        # Start the monitor
        start_monitor(ec2_resource, log_url)
        print "Done!"
    except requests.exceptions.ConnectionError:
        print "Connection refused"
except Exception as ex:
    print ex
    print "Unable to pass the authentication"
