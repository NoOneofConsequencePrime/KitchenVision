import argparse
import sys
import os

from ceir_crop import Crop
from ceir_detect import detect
from recognition import ceir_recognize


def main():
    # Set up argument parsing
    parser = argparse.ArgumentParser(description="Process images in sequence for cropping, detection, and prediction.")

    # Add arguments for each step (image files for all steps, and text file for the third step)
    parser.add_argument('--crop', type=str, required=True, help='Path to the image file for cropping.')
    parser.add_argument('--detect', type=str, required=True, help='Path to the image file for detection.')
    parser.add_argument('--predict_image', type=str, required=True, help='Path to the image file for prediction.')
    parser.add_argument('--predict_label', type=str, required=True, help='Path to the text label file for prediction.')

    # Parse the arguments
    args = parser.parse_args()

    # 1. Crop the image
    print(f"Cropping image: {args.crop}")
    crop = Crop(args.crop)  # Assuming Crop is a class from your first code
    crop.main()

    # 2. Detect objects in the image
    temp = os.getcwd()
    print(f"Detecting objects in image: {args.detect}")
    sys.path.append(os.path.join(os.getcwd(), 'detection/'))
    detect(image_path=args.detect)

    print(temp)

    # 3. Predict using the image and label file
    print(f"Predicting on image: {args.predict_image} with label file: {args.predict_label}")
    ceir_recognize.load_images_to_predict(args.predict_image, args.predict_label)


if __name__ == "__main__":
    main()
