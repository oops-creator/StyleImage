package com.facepace.styleimage;

import android.provider.ContactsContract;
import android.util.Log;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.opencv.imgproc.Imgproc.bilateralFilter;


public class Cartoonize {
    Mat src = new Mat();
    Mat dst = new Mat();
    Mat orginal = new Mat();
    String TAG = "com.facepace.styleimage.Cartoonize";
    int num_down = 2;
    int num_bilat = 7;

    public Cartoonize(Mat mat){
        src = mat;
        orginal = src.clone();
    }

    public byte[] makeCartoon() throws IOException {

        //TODO Add spinner
        if(src.empty()){
            Log.d(TAG, "makeCartoon: No image");
            System.exit(0);
        }
        dst = src.clone();

        for(int i = 0;i<num_down;i++){
            Imgproc.pyrDown(src,dst);
            src =  dst.clone();
        }

        for(int i = 0;i<num_bilat;i++){
            bilateralFilter ( src, dst, 9, 9, 7 );
            src = dst.clone();
        }

        for(int i = 0;i<num_down;i++){
            Imgproc.pyrUp(src,dst);
            src =  dst.clone();
        }

        Mat edited = src.clone();


        Imgproc.cvtColor(orginal,edited,Imgproc.COLOR_BGR2GRAY);
        Imgproc.medianBlur(edited,edited,9);
        Imgproc.adaptiveThreshold(edited,edited,255,Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY,9,2);
        Imgproc.cvtColor(edited,edited,Imgproc.COLOR_GRAY2BGR);
        Core.bitwise_and(edited,dst,dst);



        MatOfByte mob=new MatOfByte();
        Imgcodecs.imencode(".jpeg" ,dst , mob);

        byte bytesData[]=mob.toArray();
        /*FileOutputStream fos = new FileOutputStream(pictureFile);
        fos.write(bytesData);
        fos.close();*/

        return bytesData;
    }



}
