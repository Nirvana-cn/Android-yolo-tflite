package com.example.yolo;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Detect {
    private static final String MODEL_PATH = "yolov3-tiny.tflite";
    private static final int BYTE_SIZE_OF_FLOAT = 4;
    private static final int IMAGE_SIZE = 416;
    private static final int CHANNEL = 3;
    private static final int INPUT_SIZE = IMAGE_SIZE * IMAGE_SIZE * CHANNEL * BYTE_SIZE_OF_FLOAT;
    private static final int RESULT_LENGTH = 10;

    private static Detect mInstance;

    private Interpreter tfLite;
    private float[][][] result;

    public static Detect getInstance() {
        if (mInstance == null) {
            mInstance = new Detect();
        }

        return mInstance;
    }

    private Detect() {

    }

    public void initialize(AssetManager assetManager) {
        try {
            tfLite = new Interpreter(loadModelFile(assetManager));

            result = new float[1][2535][85];
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void recognize(Bitmap bitmap) {
        ByteBuffer inputBuffer = convertBitmapToByteBuffer(bitmap);
        tfLite.run(inputBuffer, result);
    }

    public void getResult() {
        for (int i = 0; i < 2535; i++) {
            float[] temp = result[0][i];
            for (int j = 5; j < 85; j++) {
                if (temp[j] > 0.5) {
                    Log.d("test", temp[j] + "");
                    Log.d("test", "i: "+ i + ", j " + j);
                    Log.d("test", temp[0] + " left");
                    Log.d("test", temp[1] + " top");
                    Log.d("test", temp[2] + " right");
                    Log.d("test", temp[3] + " bottom");
                }
            }
        }
    }

    private MappedByteBuffer loadModelFile(AssetManager assetManager) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(MODEL_PATH);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declareLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declareLength);
    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(INPUT_SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] pixels = new int[IMAGE_SIZE * IMAGE_SIZE];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int pixel : pixels) {
            float rChannel = (pixel >> 16) & 0xFF;
            float gChannel = (pixel >> 8) & 0xFF;
            float bChannel = (pixel) & 0xFF;
            float pixelValue = (rChannel + gChannel + bChannel) / 3 / 255.f;
            byteBuffer.putFloat(pixelValue);
        }
        return byteBuffer;
    }
}
