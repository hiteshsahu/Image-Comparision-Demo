package com.hiteshsahu.imagecomparer.demo.model;

/**
 * Created by Hitesh on 19-09-2016.
 */
public class ImageDataHolder {

    private int[] firstRGB, secondRGB;

    public int[] getFirstRGB() {
        return firstRGB;
    }

    public void setFirstRGB(int[] firstRGB) {
        this.firstRGB = firstRGB;
    }

    public int[] getSecondRGB() {
        return secondRGB;
    }

    public void setSecondRGB(int[] secondRGB) {
        this.secondRGB = secondRGB;
    }


    /**
     * Singleton
     */
    private static ImageDataHolder ourInstance = new ImageDataHolder();

    public static ImageDataHolder getInstance() {
        return ourInstance;
    }

    private ImageDataHolder() {
    }
}
