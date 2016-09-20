package com.hiteshsahu.imagecomparer.demo.domain.comparer;

import android.graphics.Color;
import android.util.Log;


//import android.util.Log;

/**
 * This class is used to process integer arrays containing RGB data and detects
 * motion.
 *
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class RgbMotionDetection {

    // Specific settings
    private static final int mPixelThreshold = 50; // Difference in pixel (RGB)
    private static final int mThreshold = 10000; // Number of different pixels
    private static final String TAG = RgbMotionDetection.class.getSimpleName();
    private static final boolean DEBUG = true;
    // (RGB)

    private static int[] baseImage = null;
    private static int baseImageWidth = 0;
    private static int baseImageHeight = 0;

    public static void setBaseImage(int[] mPrevious, int mPreviousWidth, int mPreviousHeight) {
        RgbMotionDetection.baseImage = mPrevious;
        RgbMotionDetection.baseImageWidth = mPreviousWidth;
        RgbMotionDetection.baseImageHeight = mPreviousHeight;

    }

    protected static boolean isDifferent(int[] secondImage, int width, int height) {
        if (secondImage == null) throw new NullPointerException();

        if (baseImage == null) return false;
        if (secondImage.length != baseImage.length) return true;
        if (baseImageWidth != width || baseImageHeight != height) return true;

        int totDifferentPixels = 0;
        for (int i = 0, ij = 0; i < height; i++) {
            for (int j = 0; j < width; j++, ij++) {
                int pix = (0xff & (secondImage[ij]));
                int otherPix = (0xff & (baseImage[ij]));

                // Catch any pixels that are out of range
                if (pix < 0) pix = 0;
                if (pix > 255) pix = 255;
                if (otherPix < 0) otherPix = 0;
                if (otherPix > 255) otherPix = 255;

                if (Math.abs(pix - otherPix) >= mPixelThreshold) {
                    totDifferentPixels++;

                    // Paint different pixel red
                    baseImage[ij] = Color.RED;
                }
            }
        }
        if (totDifferentPixels <= 0) totDifferentPixels = 1;
        boolean different = totDifferentPixels > mThreshold;

        if (DEBUG) {
            int size = height * width;
            int percent =
                    100 / (size / totDifferentPixels);
            String output =
                    "Number of different pixels: " + totDifferentPixels + "> " + percent
                            + "%";
            if (different) {
                Log.e(TAG, output);
            } else {
                Log.d(TAG,
                        output);
            }
        }
        return different;
    }

    /**
     * {@inheritDoc}
     */
    public int[] getPrevious() {
        return ((baseImage != null) ? baseImage.clone() : null);
    }

    /**
     * Detect motion comparing RGB pixel values. {@inheritDoc}
     */
    public boolean detect(
            int[] secondRGB_Array, int secondImageWidth, int secondImageHeight) {

        long startDetection = System.currentTimeMillis();

        boolean motionDetected = isDifferent(secondRGB_Array, secondImageWidth, secondImageHeight);

        long endDetection = System.currentTimeMillis();

        Log.d(TAG, "Detection Benchmark " + (endDetection - startDetection));

 /*       // Replace the current image with the previous.
        baseImage = secondRGB_Array;
        baseImageWidth = secondImageWidth;
        baseImageHeight = secondImageHeight;*/

        return motionDetected;
    }
}
