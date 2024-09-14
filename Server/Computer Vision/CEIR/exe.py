import argparse
import sys
import os
import subprocess

if __name__=="__main__":
    # Set up argument parsing
    parser = argparse.ArgumentParser(description="Process images in sequence for cropping, detection, and prediction.")

    # Add arguments for each step (image files for all steps, and text file for the third step)
    parser.add_argument('--image', type=str, required=True, help='Path to the image file for cropping.')
    args = parser.parse_args()
    img = args.image

    command = f"python run.py --crop images/{img}.jpg --detect result/step1/image/{img}.jpg --predict_image result/step2/image/{img}.jpg --predict_label result/step2/label/{img}.txt"
    subprocess.run(command, shell=True, check=True, text=True)
