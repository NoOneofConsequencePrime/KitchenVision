# -*- coding: utf-8 -*-
'''
Stage 3: recognition for predicting
Last time for updating: 14/09/2024
'''


import torch
import os, sys, time
from torch.autograd import Variable
sys.path.append(os.path.join(os.getcwd(), 'recognition/'))
import ut
import dt
from PIL import Image
import models.crnn as crnn



def sort_lines(txt_file):
    newlist = []
    f = open(txt_file)
    for line in f.readlines():
        line = line.strip()
        parts = line.split(',')
        if len(parts) > 1:  # Ensure there are at least 2 elements in the list
            newlist.append([int(parts[1]), line])
    sortedlines = sorted(newlist, key=(lambda x: x[0]))
    new_txt = []
    for line in sortedlines:
        new_txt.append(line[1])
    return new_txt


def predict_this_box(image, model, alphabet):
    converter = ut.strLabelConverter(alphabet)
    transformer = dt.resizeNormalize((200, 32))
    image = transformer(image)
    if torch.cuda.is_available():
        image = image.cuda()
    image = image.view(1, *image.size())
    image = Variable(image)

    model.eval()
    preds = model(image)

    _, preds = preds.max(2)
    preds = preds.transpose(1, 0).contiguous().view(-1)

    preds_size = Variable(torch.IntTensor([preds.size(0)]))
    raw_pred = converter.decode(preds.data, preds_size.data, raw=True)
    sim_pred = converter.decode(preds.data, preds_size.data, raw=False)
    return sim_pred


def load_images_to_predict(image_path, label_path):
    sys.path.append(os.path.join(os.getcwd(), 'recognition/'))
    start = time.time()
    print('load_images_to_predict')
    # load model
    main_path = os.path.abspath(os.path.join(os.getcwd()))
    model_path = os.path.join(main_path, 'recognition/save/recognition_model.pth')
    alphabet = '0123456789' \
               ',.:(%$!^&-/);<~|`>?+=_[]{}"\'@#*' \
               'abcdefghijklmnopqrstuvwxyz' \
               'ABCDEFGHIJKLMNOPQRSTUVWXYZ\ '
    imgH = 32 # should be 32
    nclass = len(alphabet) + 1
    nhiddenstate = 256

    model = crnn.CRNN(imgH, 1, nclass, nhiddenstate)
    if torch.cuda.is_available():
        model = model.cuda()
    print('loading pretrained model from %s' % model_path)
    model.load_state_dict({k.replace('module.',''):v for k,v in torch.load(model_path, map_location='cpu').items()})

    # load image
    new_image_path = os.path.join(main_path, image_path)
    image = Image.open(new_image_path).convert('L')
    words_list = []
    label = label_path
    txt = sort_lines(label)
    for line in txt:
        box = line.split(',')
        crop_image = image.crop((int(box[0]), int(box[1]), int(box[4]), int(box[5])))
        words = predict_this_box(crop_image, model, alphabet)
        words_list.append(words.upper())
    result_path = os.path.join(main_path, 'result/step3/')
    save_path = result_path + image_path.split('/')[-1][:-3] + 'txt'
    cost_time = (time.time() - start)
    print("cost time: {:.2f}s".format(cost_time))
    with open(save_path, 'w+') as result:
        for line in words_list:
            result.writelines(line+'\n')
    print('Recognize Finished.')
    print('Created Text: ', save_path)


if __name__ == "__main__":
    sys.path.append(os.path.join(os.getcwd(), 'recognition/'))
    load_images_to_predict('../result/step1/image/recipe2.jpg', 'result/step2/label/recipe2.txt')
