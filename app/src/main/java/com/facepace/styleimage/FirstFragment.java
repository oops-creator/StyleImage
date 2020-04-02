package com.facepace.styleimage;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class FirstFragment extends Fragment {

    private Camera mCamera;
    private CameraPreview mPreview;
    private FrameLayout preview;





//--------------------------------------------------------------------------------------------------




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(getContext(), mCamera);
        preview = (FrameLayout) getView().findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        int CameraId = Camera.getNumberOfCameras();
        CameraPreview.setCameraDisplayOrientation(getActivity(),CameraId-1,mCamera );



        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get an image from the camera
                mCamera.autoFocus(CameraPreview.myAutoFocusCallback);
                //TODO Create an indeterminate porgress bar
                //ProgressBar pb = (ProgressBar)view.findViewById(R.id.indeterminateBar);
                //pb.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(),"Please wait while the image is processed",Toast.LENGTH_SHORT).show();

                mCamera.takePicture(null, null, mPicture);
            }
        });



    }

//--------------------------------------------------------------------------------------------------


    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "StyleImage");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it d oes not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("StyleImage", "failed to create directory");
                return null;
            }
        }


        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }




    private String TAG = "FirstFragment";
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                Mat mat = Imgcodecs.imdecode(new MatOfByte(data), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
                Cartoonize cartoonizer = new Cartoonize(mat);
                data = cartoonizer.makeCartoon();
                fos.write(data);
                fos.close();
                Toast.makeText(getContext(),"Image stored in " + pictureFile,Toast.LENGTH_SHORT).show();

            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
                Toast.makeText(getContext(),"Error occurred",Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
                Toast.makeText(getContext(),"Error occurred",Toast.LENGTH_SHORT).show();

            }
        }
    };





    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            //TODO Camera Error
        }
        return c; // returns null if camera is unavailable
    }







//--------------------------------------------------------------------------------------------------



    @Override
    public void onDestroy() {
        super.onDestroy();
        mCamera.release();
    }
}
