package com.example.yolo;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class ImageUtils {
    private static final int IMAGE_SIZE = 416;

    public static Bitmap prepareBitmap(Bitmap old) {
        int width = old.getWidth();
        int height = old.getHeight();

        float scaleWidth = ((float) IMAGE_SIZE) / width;
        float scaleHeight = ((float) IMAGE_SIZE) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(old, 0, 0, width, height, matrix, true);
    }
}
