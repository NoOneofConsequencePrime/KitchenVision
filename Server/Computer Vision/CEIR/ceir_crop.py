# -*- coding: UTF-8 -*-
'''
Stage 1: preprocessing for predicting
Last time for updating: 14/09/2024
'''


import os
import cv2
import numpy as np
import argparse

import os
import cv2
import numpy as np


class Crop():
    def __init__(self, path):
        self.path = path

    def get_image(self, path):
        img = cv2.imread(path)
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        return img, gray

    def correct_perspective(self, img):
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        edges = cv2.Canny(gray, 50, 150)
        contours, _ = cv2.findContours(edges, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

        if contours:
            # Find the largest contour and approximate it to a polygon
            largest_contour = max(contours, key=cv2.contourArea)
            epsilon = 0.02 * cv2.arcLength(largest_contour, True)
            approx = cv2.approxPolyDP(largest_contour, epsilon, True)

            if len(approx) == 4:
                # Get the four corners of the receipt
                points = np.array(approx).reshape(-1, 2)
                rect = self.order_points(points)
                (tl, tr, br, bl) = rect
                width = max(
                    np.linalg.norm(br - bl),
                    np.linalg.norm(tr - tl)
                )
                height = max(
                    np.linalg.norm(tr - br),
                    np.linalg.norm(bl - tl)
                )
                dst = np.array([
                    [0, 0],
                    [width - 1, 0],
                    [width - 1, height - 1],
                    [0, height - 1]
                ], dtype="float32")

                M = cv2.getPerspectiveTransform(rect, dst)
                warped = cv2.warpPerspective(img, M, (int(width), int(height)))
                return warped
        return img

    def order_points(self, pts):
        rect = np.zeros((4, 2), dtype="float32")
        s = pts.sum(axis=1)
        rect[0] = pts[np.argmin(s)]
        rect[2] = pts[np.argmax(s)]
        diff = np.diff(pts, axis=1)
        rect[1] = pts[np.argmin(diff)]
        rect[3] = pts[np.argmax(diff)]
        return rect

    # gradient
    def step1(self, gray):
        blurred = cv2.GaussianBlur(gray, (9, 9), 0)
        # Sobel_gradient
        gradX = cv2.Sobel(blurred, ddepth=cv2.CV_32F, dx=1, dy=0)
        gradY = cv2.Sobel(blurred, ddepth=cv2.CV_32F, dx=0, dy=1)
        gradient = cv2.subtract(gradX, gradY)
        gradient = cv2.convertScaleAbs(gradient)
        # thresh_and_blur
        blurred = cv2.GaussianBlur(gradient, (9, 9), 0)
        (_, thresh) = cv2.threshold(blurred, 80, 255, cv2.THRESH_BINARY)
        return thresh

    # Need to set the ellipse size at first and do morphological thing.
    def step2(self, thresh):
        kernel = cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (int(thresh.shape[1] / 40), int(thresh.shape[0] / 18)))
        morpho_image = cv2.morphologyEx(thresh, cv2.MORPH_CLOSE, kernel)
        morpho_image = cv2.erode(morpho_image, None, iterations=1)
        morpho_image = cv2.dilate(morpho_image, None, iterations=1)
        return morpho_image

    # Get contour's points, draw Contours and crop image.
    def step3(self, morpho_image, original_img):
        cnts, hierarchy = cv2.findContours(morpho_image.copy(),
                                           cv2.RETR_LIST,
                                           cv2.CHAIN_APPROX_SIMPLE)
        if cnts:
            c = sorted(cnts, key=cv2.contourArea, reverse=True)[0]
            rect = cv2.minAreaRect(c)
            box = np.int32(cv2.boxPoints(rect))
            draw_img = cv2.drawContours(original_img.copy(), [box], -1, (0, 0, 255), 3)
            height = original_img.shape[0]
            weight = original_img.shape[1]
            Xs = [i[0] for i in box]
            Ys = [i[1] for i in box]
            x1 = max(min(Xs), 0)
            x2 = min(max(Xs), weight)
            y1 = max(min(Ys), 0)
            y2 = min(max(Ys), height)
            hight = y2 - y1
            width = x2 - x1
            crop_img = original_img[y1:y1 + hight, x1:x1 + width]
            return draw_img, crop_img, x1, y1
        return original_img, original_img, 0, 0

    def main(self):
        file_path = self.path
        original_img, gray = self.get_image(file_path)

        # Correct perspective before further processing
        corrected_img = self.correct_perspective(original_img)
        corrected_gray = cv2.cvtColor(corrected_img, cv2.COLOR_BGR2GRAY)

        if original_img.shape[1] > 990:
            gradient = self.step1(corrected_gray)
            morpho_image = self.step2(gradient)
            draw_img, crop_img, weight, height = self.step3(morpho_image, corrected_img)
        else:
            crop_img = corrected_img
            draw_img = corrected_img

        new_name = file_path
        name = new_name.split('/')[-1]
        main_path = os.path.abspath(os.path.join(os.getcwd()))
        image_path = os.path.join(main_path, 'result/step1/image/')
        os.makedirs(image_path, exist_ok=True)
        image_save_path = os.path.join(image_path, name)
        cv2.imwrite(image_save_path, crop_img)
        image_save_draw = os.path.join(image_path, 'draw_' + name)
        cv2.imwrite(image_save_draw, draw_img)
        print('Preprocess Finished.')
        print('Created Image: ', image_save_path)
        print('Created Image: ', image_save_draw)

if __name__ == '__main__':
    crop = Crop('images/r8.jpg')
    crop.main()