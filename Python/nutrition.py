
# === common imports ===
import requests
import json
import uuid
import logging

logger = logging.getLogger()
logger.setLevel(logging.INFO)

# === aws imports ===
import boto3
from boto3 import resource

# === azure imports ===
import httplib, urllib

# === gcp imports ===
# Imports the Google Cloud client library

from google.cloud import vision

from google.cloud.vision import types

from oauth2client.client import GoogleCredentials

# === Constants ===

search_url = 'https://api.nal.usda.gov/ndb/search/'
report_url = 'https://api.nal.usda.gov/ndb/nutrients/'

API_KEY = ""
HEADER = {"content-type":"application/json"}


AZURE_SUBSCRIPTION_KEY = ''

# key = raw_input("Please enter the food: ")
# key = key+", raw"


# Nutrition Facts
# 2000 kcal
# 2000 * 1.2 / 2000 * 0.8
ENERGY_UPPER = 800
ENERGY_LOWER = 533.33

# Protein, g  46 - 56
# 46 /3 - 56 /3
# Protein, %kcal 10 - 35
# 15.33 * 0.8 / 18.67 * 1.2
PROTEIN_UPPER = 22.4
PROTEIN_LOWER = 12.26

# Total lipid (fat), %kcal 20 -35
# Saturated Fat, % kcal <10%
# 233.33 * 1.2 / 133.33 * 0.8
FAT_UPPER = 279.99
FAT_LOWER = 106.664

# Carbohydrate, g 130
# Carbohydrate, % kcal
# 43.33 * 1.2 / 43.33 * 0.8
CARBOHYDRATE_UPPER = 51.99
CARBOHYDRATE_LOWER = 34.67

# Sugars, % kcal <10%
SUGAR_UPPER = 66.67

# Fiber, total dietary, g 28 - 33.6
# 11.2 * 1.2 / 9.33 * 0.8
FIBER_UPPER = 13.44
FIBER_LOWER = 7.46

# Potassium, K, mg 4,700
# 1566.67 * 1.2 / 1566.67 * 0.8
POTASSIUM_UPPER = 1880
POTASSIUM_LOWER = 1253.336

# Sodium, Na, mg 2,300
# 767.33 * 1.2 / 767.33 * 0.8
SODIUM_UPPER = 920.796
SODIUM_LOWER = 613.87



# The boto3 dynamoDB resource
dynamodb_resource = resource('dynamodb',region_name='us-east-1')


def read_table_item(table_name, pk_name, pk_value):
    """
    Return item read by primary key.
    """
    table = dynamodb_resource.Table(table_name)
    response = table.get_item(Key={pk_name: pk_value})

    return response


def add_item(table_name, col_dict):
    """
    Add one item (row) to table. col_dict is a dictionary {col_name: value}.
    """
    table = dynamodb_resource.Table(table_name)
    response = table.put_item(Item=col_dict)

    return response


def evaluate_health_report(nutrients):
    """
    Evaluate health score based on the limits
    :param nutrients:
    :return:
    """
    total_score = 0
    try:
        protein = nutrients["Protein"]
        logger.info("Protein: {}".format(protein))
        if PROTEIN_LOWER < float(protein) < PROTEIN_UPPER:
            logger.info("[HEALTHY] Your protein meets the daily standard.")
            total_score = total_score + 12.5
        elif float(protein) < PROTEIN_LOWER:
            logger.info("[UNHEALTHY] You need to have more protein.")
            total_score = total_score + score_percentage((PROTEIN_LOWER - float(protein)), PROTEIN_LOWER)
        elif float(protein) > PROTEIN_UPPER:
            logger.info("[UNHEALTHY] You have too much protein.")
            total_score = total_score + score_percentage((float(protein) - PROTEIN_UPPER), PROTEIN_UPPER)
    except Exception:
        logger.info("No protein data found")

    try:
        sugar = nutrients["Sugar"]
        logger.info("Sugar: {}".format(sugar))
        if float(sugar) < SUGAR_UPPER:
            logger.info("[HEALTHY] Your sugar meets the daily standard.")
            total_score = total_score + 12.5
        else:
            logger.info("[UNHEALTHY] You have too much sugar.")
            total_score = total_score + score_percentage((float(sugar) - SUGAR_UPPER), SUGAR_UPPER)
    except Exception:
        logger.info("No sugar data found")

    try:
        fat = nutrients["Fat"]
        logger.info("Total lipid: {}".format(fat))
        if FAT_LOWER < float(fat) < FAT_UPPER:
            logger.info("[HEALTHY] Your total lipid meets the daily standard.")
            total_score = total_score + 12.5
        elif float(fat) < FAT_LOWER:
            logger.info("[UNHEALTHY] You need to have more total lipid.")
            total_score = total_score + score_percentage((FAT_LOWER - float(fat)), FAT_LOWER)
        elif FAT_UPPER < float(fat):
            logger.info("[UNHEALTHY] You have too much total lipid.")
            total_score = total_score + score_percentage((float(fat) - FAT_UPPER), FAT_UPPER)
    except Exception:
        logger.info("No total lipid data found")

    try:
        carbohydrate = nutrients["Carbohydrate"]
        logger.info("Carbohydrate: {}".format(carbohydrate))
        if CARBOHYDRATE_LOWER < float(carbohydrate) < CARBOHYDRATE_UPPER:
            logger.info("[HEALTHY] Your carbohydrate meets the daily standard.")
            total_score = total_score + 12.5
        elif float(carbohydrate) < CARBOHYDRATE_LOWER:
            logger.info("[UNHEALTHY] You need to have more carbohydrate.")
            total_score = total_score + score_percentage((CARBOHYDRATE_LOWER - float(carbohydrate)), CARBOHYDRATE_LOWER)
        elif CARBOHYDRATE_UPPER < float(carbohydrate):
            logger.info("[UNHEALTHY] You have too much carbohydrate.")
            total_score = total_score + score_percentage((float(carbohydrate) - CARBOHYDRATE_UPPER), CARBOHYDRATE_UPPER)
    except Exception:
        logger.info("No carbohydrate data found")

    try:
        potassium = nutrients["Potassium"]
        logger.info("Potassium: {}".format(potassium))
        if POTASSIUM_LOWER < float(potassium) < POTASSIUM_UPPER:
            logger.info("[HEALTHY] Your potassium meets the daily standard.")
            total_score = total_score + 12.5
        elif float(potassium) < POTASSIUM_LOWER:
            logger.info("[UNHEALTHY] You need to have more potassium.")
            total_score = total_score + score_percentage((POTASSIUM_LOWER - float(potassium)), POTASSIUM_LOWER)
        elif POTASSIUM_UPPER < float(potassium):
            logger.info("[UNHEALTHY] You have too much potassium.")
            total_score = total_score + score_percentage((float(potassium) - POTASSIUM_UPPER), POTASSIUM_UPPER)
    except Exception:
        logger.info("No potassium data found")

    try:
        energy = nutrients["Energy"]
        logger.info("Energy: {}".format(energy))
        if ENERGY_LOWER < float(energy) < ENERGY_UPPER:
            logger.info("[HEALTHY] Your calories meets the daily standard.")
            total_score = total_score + 12.5
        elif float(energy) < ENERGY_LOWER:
            logger.info("[UNHEALTHY] You need to have more calories.")
            total_score = total_score + score_percentage((ENERGY_LOWER - float(energy)), ENERGY_LOWER)
        elif ENERGY_UPPER < float(energy):
            logger.info("[UNHEALTHY] You have too much calories.")
            total_score = total_score + score_percentage((float(energy) - ENERGY_UPPER), ENERGY_UPPER)
    except Exception:
        logger.info("No energy data found")

    try:
        fiber = nutrients["Fiber"]
        logger.info("Fiber: {}".format(fiber))
        if FIBER_LOWER < float(fiber) < FIBER_UPPER:
            logger.info("[HEALTHY] Your fiber meets the daily standard.")
            total_score = total_score + 12.5
        elif float(fiber) < FIBER_LOWER:
            logger.info("[UNHEALTHY] You need to have more fiber.")
            total_score = total_score + score_percentage((FIBER_LOWER - float(fiber)), FIBER_LOWER)
        elif FIBER_UPPER < float(fiber):
            logger.info("[UNHEALTHY] You have too much fiber.")
            total_score = total_score + score_percentage((float(fiber) - FIBER_UPPER), FIBER_UPPER)
    except Exception:
        logger.info("No fiber data found")

    try:
        sodium = nutrients["Sodium"]
        logger.info("Sodium: {}".format(sodium))
        if SODIUM_LOWER < float(sodium) < SODIUM_UPPER:
            logger.info("[HEALTHY] Your sodium meets the daily standard.")
            total_score = total_score + 12.5
        elif float(sodium) < SODIUM_LOWER:
            logger.info("[UNHEALTHY] You need to have more sodium.")
            total_score = total_score + score_percentage((SODIUM_LOWER - float(sodium)), SODIUM_LOWER)
        elif SODIUM_UPPER < float(sodium):
            logger.info("[UNHEALTHY] You have too much sodium.")
            total_score = total_score + score_percentage((float(sodium) - SODIUM_UPPER), SODIUM_UPPER)
    except Exception:
        logger.info("No sodium data found")

    return total_score


def check_label(label):
    """
    Check whether a word is relevant
    :param label:
    :return:
    """
    if label in open('keywords.txt').read():
        return True
    else:
        return False


def score_percentage(difference, bound):
    """
    Calculate the score
    :param difference:
    :param bound:
    :return:
    """
    percentage = (difference / bound) * 100
    logger.info("Percentage: {} %".format(percentage))
    if percentage > 200 or percentage < 0:
        logger.info("Score: 0")
        return 0
    elif 180 < percentage < 200 or 0 < percentage < 20:
        logger.info("Score: 2.5")
        return 2.5
    elif 160 < percentage < 180 or 20 < percentage < 40:
        logger.info("Score: 5")
        return 5
    elif 140 < percentage < 160 or 40 < percentage < 60:
        logger.info("Score: 7.5")
        return 7.5
    elif 120 < percentage < 140 or 60 < percentage < 80:
        logger.info("Score: 10")
        return 10
    elif 100 < percentage < 120 or 80 < percentage < 100:
        logger.info("Score: 12.5")
        return 12.5
    else:
        return 0


def scoring(labels):
    """
    Iterate the label list and calculate scores
    :param labels:
    :return:
    """
    logger.info("Labels: {}".format(labels))
    logger.info(type(labels))

    new_labels = {}

    image_id = uuid.uuid4()

    info_labels = []

    for label in labels:
        # logger.info("Current index: {}".format(index)
        logger.info("{}".format(label))
        label_name = label.get("Name").lower()
        logger.info("Current label: {}".format(label_name))
        if "food" in label_name:
            logger.info("Found food")
            continue
        else:
            logger.info("Label name: {}".format(label_name))
            label_exist = check_label(label_name)
            logger.info("Label exist?: {}".format(label_exist))
            if label_exist is False:
                if label.get("Confidence") > 0.5:
                    new_labels[label_name] = label.get("Confidence")
                    info_labels.append(
                        {"platform": label["Provider"], "label": label_name, "confidence": str(label["Confidence"])})
        logger.info("New List: {}".format(new_labels))

    add_item("label_info", {"image_id": image_id, "labels": info_labels})

    client_nutrition = {"Protein": 0, "Sugar": 0, "Fat": 0, "Carbohydrate": 0, "Potassium": 0, "Energy": 0, "Fiber": 0,
                        "Sodium": 0}

    for key in new_labels.keys():
        logger.info("Going to get info of food: {}".format(key))

        # Search food Confidence in the database
        search_params = {"q": key, "max": "25", "offset": "0", "api_key": API_KEY}
        search_results = requests.get(search_url, params=search_params, headers=HEADER).json()
        logger.info(type(search_results))
        logger.info(search_results)
        first_result = search_results["list"]["item"][0]
        logger.info("First result: {}".format(first_result))

        # 203 Protein
        # 204 Total lipid (fat)
        # 205 Carbohydrate
        # 208 Energy
        # 269 Sugars, total
        # 291 Fiber, total dietary
        # 306 Potassium, K
        # 307 Sodium, Na
        report_params = {"nutrients": ["203", "204", "205", "208", "269", "291", "306", "307"],
                         "ndbno": first_result["ndbno"], "max": 25, "offset": 0, "api_key": API_KEY}

        report_results = requests.get(report_url, params=report_params, headers=HEADER).json()
        logger.info(report_results)
        nutrients = report_results["report"]["foods"][0]["nutrients"]
        try:
            user_protein = client_nutrition.get("Protein")
            protein = float(nutrients[0]["value"])
            client_nutrition["Protein"] = (user_protein + protein)
            logger.info("Current protein is: {}".format(client_nutrition["Protein"]))
        except Exception:
            logger.info("Protein data is not available.")

        try:
            user_sugar = client_nutrition.get("Sugar")
            sugar = float(nutrients[1]["value"])
            client_nutrition["Sugar"] = (user_sugar + sugar)
            logger.info("Current sugar is: {}".format(client_nutrition["Sugar"]))
        except Exception:
            logger.info("Sugar data is not available.")

        try:
            user_fat = client_nutrition.get("Fat")
            fat = float(nutrients[2]["value"])
            client_nutrition["Fat"] = (user_fat + fat)
            logger.info("Current fat is: {}".format(client_nutrition["Fat"]))
        except Exception:
            logger.info("Total lipid data is not available.")

        try:
            user_carbohydrate = client_nutrition.get("Carbohydrate")
            carbohydrate = float(nutrients[3]["value"])
            client_nutrition["Carbohydrate"] = (user_carbohydrate + carbohydrate)
            logger.info("Current carbohydrate is: {}".format(client_nutrition["Carbohydrate"]))
        except Exception:
            logger.info("Carbohydrate data is not available.")

        try:
            user_potassium = client_nutrition.get("Potassium")
            potassium = float(nutrients[4]["value"])
            client_nutrition["Potassium"] = (user_potassium + potassium)
            logger.info("Current potassium is: {}".format(client_nutrition["Potassium"]))
        except Exception:
            logger.info("Potassium data is not available.")

        try:
            user_energy = client_nutrition.get("Energy")
            energy = float(nutrients[5]["value"])
            client_nutrition["Energy"] = (user_energy + energy)
            logger.info("Current energy is: {}".format(client_nutrition["Energy"]))
        except Exception:
            logger.info("Energy data is not available.")

        try:
            user_fiber = client_nutrition.get("Fiber")
            fiber = float(nutrients[6]["value"])
            client_nutrition["Fiber"] = (user_fiber + fiber)
            logger.info("Current fiber is: {}".format(client_nutrition["Fiber"]))
        except Exception:
            logger.info("Fiber data is not available.")

        try:
            user_sodium = client_nutrition.get("Sodium")
            sodium = float(nutrients[7]["value"])
            client_nutrition["Sodium"] = (user_sodium + sodium)
            logger.info("Current sodium is: {}".format(client_nutrition["Sodium"]))
        except Exception:
            logger.info("Sodium data is not available.")

    score = evaluate_health_report(client_nutrition)

    logger.info("Final score is: {}".format(score))

    add_item("label_score", {"image_id": image_id, "score": str(score)})


def label_handler(event, context):
    """
    handler for lambda feature
    :param event:
    :param context:
    :return:
    """
    label_list = []

    # ===================== AWS ===================
    # Get required information from event message
    message = json.loads(event['Records'][0]['Sns']['Message'])
    image_name = message['Records'][0]['s3']['object']['key']
    bucket_name = message['Records'][0]['s3']['bucket']['name']

    aws_region = 'us-east-1'
    s3_client = boto3.client('s3')
    s3_client.put_object_acl(Bucket=bucket_name, Key=image_name, ACL='public-read')
    # make picture public
    url = 'http://s3.amazonaws.com/' + bucket_name + '/' + image_name

    # Send to rekognition
    rek_client = boto3.client('rekognition', aws_region)
    aws_response = rek_client.detect_labels(
        Image={
            "S3Object": {
                "Bucket": bucket_name,
                "Name": image_name,
            }
        },
        MaxLabels=10,
        MinConfidence=50
    )

    aws_labels = aws_response.get("Labels")
    for aws_label in aws_labels:
        label_list.append({"Name":aws_label["Name"],"Confidence":str(float(aws_label["Confidence"])/100), "Platform":"AWS"})

    logger.info("AWS Done. :)")


    # ===================== Azure ===================
    azure_headers = {
        # Request headers.
        'Content-Type': 'application/json',
        'Ocp-Apim-Subscription-Key': AZURE_SUBSCRIPTION_KEY,
    }

    azure_params = urllib.urlencode({
        # Request parameters. All of them are optional. Description,Color
        'visualFeatures': 'Tags',
        'language': 'en',
    })

    # The URL of a JPEG image to analyze.
    azure_body = "{'url':'"+url+"'}"

    try:
        # Execute the REST API call and get the response.
        conn = httplib.HTTPSConnection('westcentralus.api.cognitive.microsoft.com')
        conn.request("POST", "/vision/v1.0/analyze?%s" % azure_params, azure_body, azure_headers)
        azure_response = conn.getresponse()
        data = azure_response.read()

        # 'data' contains the JSON data. The following formats the JSON data for display.
        parsed = json.loads(data)
        tags = parsed["tags"]
        for azure_label in tags:
            label_list.append(
                {"Name": azure_label["name"], "Confidence": str(azure_label["confidence"]), "Platform": "Azure"})

        conn.close()

    except Exception as e:
        logger.info('Error:')
        logger.info(e)

    logger.info("Azure Done :)")

    # =================== GCP ==================
    credentials = GoogleCredentials.get_application_default()

    client = vision.ImageAnnotatorClient()

    image = types.Image()
    image.source.image_uri = 'http://s3.amazonaws.com/' + bucket_name + '/' + image_name
    logger.info('http://s3.amazonaws.com/' + bucket_name + '/' + image_name)

    gcp_response = client.label_detection(image=image)
    gcp_labels = gcp_response.label_annotations

    for gcp_label in gcp_labels:
        label_list.append(
            {"Name": gcp_label.description, "Confidence": str(gcp_label.score), "Platform": "GCP"})
        logger.info(gcp_label.description, str(gcp_label.score))

    logger.info("GCP Done :)")

    logger.info("Label list:{}".format(label_list))

    scoring(label_list)

# =============================================

# labels = [
#       {
#          "Name":"Avocado",
#          "Confidence":0.9929508209228516,
#       },
#       {
#          "Confidence":0.9929508209228516,
#          "Name":"Fruit"
#       },
#       {
#          "Name":"natural foods",
#          "Confidence":0.9842714667320251
#       },
#       {
#          "Name":"fruit",
#          "Confidence":0.9672513008117676
#       },
#       {
#          "Name":"apple",
#          "Confidence":0.9635712504386902
#       },
#       {
#          "Name":"produce",
#          "Confidence":0.953955352306366
#       },
#       {
#          "Name":"local food",
#          "Confidence":0.9475510716438293
#       },
#       {
#          "Name":"food",
#          "Confidence":0.9073199033737183
#       },
#       {
#          "Name":"diet food",
#          "Confidence":0.7235879898071289
#       },
#       {
#          "Name":"superfood",
#          "Confidence":0.5856496691703796
#       },
#       {
#          "Name":"mcintosh",
#          "Confidence":0.5546462535858154
#       },
#       {
#          "Confidence":0.9836429953575134,
#          "Name":"food"
#       },
#       {
#          "Confidence":0.880021870136261,
#          "hint":"food",
#          "Name":"sandwich"
#       },
#       {
#          "Confidence":0.5582974553108215,
#          "Name":"cut"
#       },
#       {
#          "Confidence":0.48052000999450684,
#          "Name":"bread"
#       },
#       {
#          "Confidence":0.2729416489601135,
#          "Name":"sliced"
#       },
#       {
#          "Confidence":0.23944500088691711,
#          "Name":"breakfast"
#       },
#       {
#          "Confidence":0.11349174380302429,
#          "Name":"fresh"
#       }
#    ]







    # energy_result = report_results["report"]["foods"][0]["nutrients"][0]
    # calorie = energy_result["value"]
    # print "{} has calories: {}".format(key,calorie)


# ============== Connect to database =================

# =============  Dashboard ====================







