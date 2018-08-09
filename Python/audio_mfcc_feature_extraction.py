import sys
sys.path.append("../")
import os
import utils
import numpy as np
import librosa


def extract_audio(config):
    '''
    Extract the audio track from original mp4 videos using ffmpeg
    :param config: configurations
    :return:
    '''
    if not os.path.exists(config.audio_root_path):
        os.mkdir(config.audio_root_path)

    #   read and concatenate train/validation/test video lists
    all_video_label_list=utils.get_video_and_label_list(config.all_train_list_filename)+\
                         utils.get_video_and_label_list(config.all_val_list_filename)+\
                         utils.get_video_and_label_list(config.all_test_list_filename)

    count = 0
    #   iterate over the video list and call system command for ffmpeg audio extraction
    for now_video_label in all_video_label_list:
        # print now_video_label
        vid_name = now_video_label[0]
        vid_full_fn = os.path.join(config.video_root_path, vid_name+config.video_file_format)
        audio_full_fn = os.path.join(config.audio_root_path, vid_name+config.audio_file_format)

        if not os.path.exists(audio_full_fn):
            #   call command line for audio track extraction
            command = "ffmpeg -y -i %s -ac 1 -f wav %s" % (vid_full_fn, audio_full_fn)
            os.system(command)
            count += 1
        else:
            print "File " + audio_full_fn + " already exist."

    print "In total number of " + str(len(all_video_label_list)) + " videos, there are " + str(count) + " files that has no audio."


def extract_mfcc(config):
    '''
    Extract the mfcc feature from audio files.
    :param config:
    :return:
    '''

    if not os.path.exists(config.mfcc_root_path):
        os.mkdir(config.mfcc_root_path)

    #   read and concatenate train/validation/test video lists
    all_video_label_list = utils.get_video_and_label_list(config.all_train_list_filename) + \
                           utils.get_video_and_label_list(config.all_val_list_filename) + \
                           utils.get_video_and_label_list(config.all_test_list_filename)

    #   you may add your code to extract mfcc features and store them in numpy files...
    for now_video_label in all_video_label_list:
        vid_name = now_video_label[0]
        audio_full_fn = os.path.join(config.audio_root_path, vid_name+config.audio_file_format)
        mfcc_full_fn = os.path.join(config.mfcc_root_path, vid_name+config.mfcc_file_format)

        if os.path.exists(audio_full_fn):
            y, sr = librosa.load(audio_full_fn)
            data = librosa.feature.mfcc(y=y, sr=sr)
            np.save(mfcc_full_fn, data)
        else:
            print "File: "+audio_full_fn+" does not exist"


if __name__=="__main__":
    pass
