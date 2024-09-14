import argparse
import os

import cv2
import imutils
import pytesseract
from imutils.perspective import four_point_transform

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "-i", "--image", type=str, required=True, help="path to input image"
    )
    args = parser.parse_args()

    # Check if image with given path exists
    if not os.path.exists(args.image):
        raise Exception("The given image does not exist.")

    # Load the image, resize and compute ratio
    img_orig = cv2.imread(args.image)
    image = img_orig.copy()
    image = imutils.resize(image, width=500)
    ratio = img_orig.shape[1] / float(image.shape[1])

    # Convert the image to grayscale, blur it slightly, and then apply edge detection
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    blurred = cv2.GaussianBlur(gray, (5, 5), 0)
    edged = cv2.Canny(blurred, 75, 200)

    # Find contours in the edge map and sort them by size in descending order
    cnts = cv2.findContours(edged.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    cnts = imutils.grab_contours(cnts)
    cnts = sorted(cnts, key=cv2.contourArea, reverse=True)

    # Initialize a contour that corresponds to the receipt outline
    receiptCnt = None
    # Loop over the contours
    for c in cnts:
        # Approximate the contour
        peri = cv2.arcLength(c, True)
        approx = cv2.approxPolyDP(c, 0.02 * peri, True)
        # If our approximated contour has four points, then we can assume we have found the outline of the receipt
        if len(approx) == 4:
            receiptCnt = approx
            break

    # If the receipt contour is empty then our script could not find the outline and we should be notified
    if receiptCnt is None:
        raise Exception("Could not find receipt outline. Try debugging your edge detection and contour steps.")

    # Apply a four-point perspective transform to the *original* image to obtain a top-down bird's-eye view of the receipt
    receipt = four_point_transform(img_orig, receiptCnt.reshape(4, 2) * ratio)

    # Apply OCR to the receipt image by assuming column data
    options = "--psm 6"
    text = pytesseract.image_to_string(cv2.cvtColor(receipt, cv2.COLOR_BGR2RGB), config=options)

    # Derive the output text file path from the image file path
    base_name = os.path.splitext(args.image)[0]
    output_file = f"{base_name}.txt"

    # Save the raw output of the OCR process to a text file
    with open(output_file, "w") as file:
        file.write(text)

    print("[INFO] Text extraction complete. Output saved to:", output_file)


if __name__ == "__main__":
    pytesseract.pytesseract.tesseract_cmd = r'C:\Program Files\Tesseract-OCR\tesseract.exe'
    main()

