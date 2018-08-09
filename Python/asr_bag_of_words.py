'''
 A "random" bag-of-words implementation using the ASR feature
'''

import os
import sys
sys.path.append("../")
import utils
import pickle
import numpy as np
from sklearn.cluster import KMeans
from sklearn.externals import joblib

def get_bow_vec(config):
    '''
    Generate ASR bag of words vector and save them.
    :param config: configurations
    :return:
    '''
    #   read and concatenate train/validation/test video lists
    all_video_label_list = utils.get_video_and_label_list(config.all_train_list_filename) + \
                           utils.get_video_and_label_list(config.all_val_list_filename) + \
                           utils.get_video_and_label_list(config.all_test_list_filename)

    #   the number of bag-of-words centers
    if not os.path.exists(config.asr_bow_root_path):
        os.mkdir(config.asr_bow_root_path)

    vocab_book = utils.read_object_from_pkl(config.cmu_asr_vocabbook_filename)
    word_len = len(vocab_book)

    for now_video_label in all_video_label_list:
        vid_name = now_video_label[0]
        asr_filename = os.path.join(config.cmu_asr_root_path,vid_name+config.cmu_asr_file_format)
        asr_bow_filename = os.path.join(config.asr_bow_root_path,vid_name+config.asr_bow_file_format)

        if os.path.exists(asr_filename):
            word_list = utils.read_object_from_pkl(asr_filename)
            asr_bow_vec = np.zeros((1, word_len))
            #   we randomly set the Bag-of-Words representation vector
            #   according to the number of words in ASR transcription file
            asr_bow_vec[0, len(word_list)%word_len] = 1

            np.save(asr_bow_filename, asr_bow_vec)
        else:
            print "File " + asr_filename + " does not exist"


def train_kmeans(config):
    '''
    Train K-means from the bag-of-words vectors.
    :param config: configurations
    :return:
    '''
    if not os.path.exists("models"):
        os.mkdir("models")

    all_video_label_list = utils.get_video_and_label_list(config.all_train_list_filename) + \
                           utils.get_video_and_label_list(config.all_val_list_filename) + \
                           utils.get_video_and_label_list(config.all_test_list_filename)

    #   the number of bag-of-words centers
    list_of_data = []
    for now_video_label in all_video_label_list:
        vid_name = now_video_label[0]
        asr_bow_filename = os.path.join(config.asr_bow_root_path, vid_name + config.asr_bow_file_format)

        if os.path.exists(asr_bow_filename):
           data = np.load(asr_bow_filename)
           for i in range(data.shape[0]):
                list_of_data.append(data[i])
        else:
            print "File: " + asr_bow_filename + " does not exist"

    array_of_data = np.array(list_of_data)
    print array_of_data.shape
    print type(array_of_data)

    print "Going to do k-means"
    data_kmean = KMeans(n_clusters=200).fit(array_of_data)
    print "K-means"
    print type(data_kmean)

    centroids = data_kmean.labels_
    print centroids, type(centroids)
    joblib.dump(data_kmean, 'models/kmeans_asr.pkl')


def asr_vectors(config):
    '''
    Generate ASR features.
    :param config:
    :return:
    '''
    if not os.path.exists(config.asr_bow_feature_path):
        os.mkdir(config.asr_bow_feature_path)

    #   read and concatenate train/validation/test video lists
    all_video_label_list = utils.get_video_and_label_list(config.all_train_list_filename) + \
                           utils.get_video_and_label_list(config.all_val_list_filename) + \
                           utils.get_video_and_label_list(config.all_test_list_filename)

    km = joblib.load('models/kmeans_asr.pkl')
    print "K-means model loaded success"

    for now_video_label in all_video_label_list:
        # print now_video_label
        vid_name = now_video_label[0]

        asr_bow_filename = os.path.join(config.asr_bow_root_path, vid_name + config.asr_bow_file_format)
        asr_feature_full_fn = os.path.join(config.asr_bow_feature_path, vid_name + config.asr_bow_feature_format)

        if os.path.exists(asr_bow_filename):
            data = np.load(asr_bow_filename)
            pred = km.predict(data)

            closest_counts = np.bincount(pred, minlength=200)
            closest_counts = closest_counts.reshape(1, closest_counts.shape[0])
            np.save(asr_feature_full_fn, closest_counts)
            print "{} feature vector done. ".format(vid_name)
        else:
            print "File: " + asr_bow_filename + " does not exist"


if __name__=="__main__":
    pass
