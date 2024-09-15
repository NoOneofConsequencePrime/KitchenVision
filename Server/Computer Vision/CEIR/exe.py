import argparse
import sys
import os
import subprocess
import shutil
import time

if __name__=="__main__":
    # # Set up argument parsing
    # parser = argparse.ArgumentParser(description="Process images in sequence for cropping, detection, and prediction.")
    #
    # # Add arguments for each step (image files for all steps, and text file for the third step)
    # parser.add_argument('--image', type=str, required=True, help='Path to the image file for cropping.')
    # args = parser.parse_args()
    # img = args.image

    # import sys;

    # print(sys.executable)
    # print(sys.path)
    # print(os.environ.get('PYTHONPATH'))
    while True:
        while True:
            if os.path.isfile(r"C:\xampp\htdocs\uploads\temp.jpg"):
                image_path = "C:/xampp/htdocs/uploads/temp.jpg"
                shutil.copy(image_path, "C:/Users/john.qing/PycharmProjects/CEIRTest/images/temp.jpg")
                time.sleep(2)
                os.remove(image_path)
                img = "temp"
                # command = f"py C:\\Users\john.qing\PycharmProjects\CEIRTest\\run.py --crop C:\\xampp\\htdocs\\uploads\\{img}.jpg --detect result/step1/image/{img}.jpg --predict_image result/step2/image/{img}.jpg --predict_label result/step2/label/{img}.txt"
                command = f"python run.py --crop images/{img}.jpg --detect result/step1/image/{img}.jpg --predict_image result/step2/image/{img}.jpg --predict_label result/step2/label/{img}.txt"
                subprocess.run(command, shell=True, check=True, text=True)
                os.remove(r"C:\Users\john.qing\PycharmProjects\CEIRTest\images\temp.jpg")
                break


            else:
                print("No input received")

        while True:
            if os.path.isfile(r"C:\Users\john.qing\PycharmProjects\CEIRTest\result\step3\temp.txt"):
                command = r"py C:\Users\john.qing\PycharmProjects\BertingBert\cohereTest.py"
                subprocess.run(command, shell=True, check=True, text=True)
                break


            else:
                print("Waiting for txt file")



    command = f"py C:\\Users\john.qing\PycharmProjects\CEIRTest\\run.py --crop C:\\xampp\\htdocs\\uploads\\temp.jpg --detect result/step1/image/{img}.jpg --predict_image result/step2/image/{img}.jpg --predict_label result/step2/label/{img}.txt"
    subprocess.run(command, shell=True, check=True, text=True)
