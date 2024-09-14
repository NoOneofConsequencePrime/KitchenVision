# CEIR
This project is for the SPIE paper - Novel Receipt Recognition with Deep Learning Algorithms.
In this paper, we propose an end-to-end novel receipt recognition system for capturing effective information from receipts (CEIR).

CEIR system demo is available at:
[CEIR Demo](http://eadst.com/ceir)

![](http://www.eadst.com/media/upload/2020/04/CEIR_DEMO_2.png)

The CEIR has three parts: preprocess, detection, recognition.

## Credit
This project is obtained from: https://github.com/eadst/CEIR/blob/master/README.md

## Modification
Modification made by James Zhao. Include testing with personal image files and implementation of parser file.

## Usage
cd the CIER folder and use: python run.py --crop images/recipe3.jpg --detect result/step1/image/recipe3.jpg --predict_image result/step2/image/recipe3.jpg --predict_label result/step2/label/recipe3.txt

