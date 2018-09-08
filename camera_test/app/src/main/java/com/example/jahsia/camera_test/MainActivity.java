package com.example.jahsia.camera_test;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import java.io.IOException;
import 	android.content.pm.PackageManager;
import 	android.Manifest;
import  java.util.List;
import com.example.jahsia.camera_test.CameraPreview;

public class MainActivity extends AppCompatActivity {

    private static final int ACTIVITY_START_CAMERA = 0;
    private ImageView _capturedPhotoView;

    private SurfaceView _camView;
    private SurfaceHolder _surfaceHolder;
    private Camera _camera;
    private boolean _camRunning = false;
    private CameraPreview _preView;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.activity_main);
        }
        catch( Exception e){
            e.printStackTrace();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        _preView = (CameraPreview)findViewById(R.id.surfaceView);

        //_preView = new CameraPreview(this, cam_view);

        //setContentView(cam_view);

        // Find the total number of cameras available
        int numberOfCameras = Camera.getNumberOfCameras();

        // Find the ID of the default camera
        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                //defaultCameraId = i;
            }
        }
        _capturedPhotoView = (ImageView)findViewById(R.id.capturedPhotoView);
        // Example of a call to a native method
        //TextView tv = (TextView) findViewById(R.id.sample_text);
        //tv.setText(stringFromJNI());
       // _surfaceHolder = _camView.getHolder();
       // _surfaceHolder.addCallback(this);
       // _surfaceHolder.setType(SurfaceHolder.);
       // _surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //If authorisation not granted for camera
        //If authorisation not granted for camera

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            //ask for authorisation
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 50);
        else
            Toast.makeText(this, "camera pressed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Open the default i.e. the first rear facing camera.
        _camera = Camera.open();
        //cameraCurrentlyLocked = defaultCameraId;
        _preView.setCamera(_camera);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public void takePhoto(View view){
        Toast.makeText(this, "camera pressed", Toast.LENGTH_SHORT).show();
       // Intent callCamCapture = new Intent();
       // callCamCapture.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(callCamCapture, ACTIVITY_START_CAMERA );

        //_camera.takePicture(null, null, null, mPictureCallback);
    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera c) {

            //Bitmap captured = new Bitmap(data);

        }
    };

    protected void onActivityResult(int code, int result, Intent data){
        if(code == ACTIVITY_START_CAMERA && result == RESULT_OK ){
            Toast.makeText(this, "got photo", Toast.LENGTH_SHORT).show();
            Bundle bdata = data.getExtras();
            Bitmap captured = (Bitmap)bdata.get("data");
            _capturedPhotoView.setImageBitmap(captured);
        }else{
            Toast.makeText(this, "no photo", Toast.LENGTH_SHORT).show();
        }
    }
/*
    private static final double MAX_ASPECT_DISTORTION = 0.15;
    private static final float ASPECT_RATIO_TOLERANCE = 0.01f;
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
        // stop the camera
        if(_camRunning){
            _camera.stopPreview(); // stop preview using stopPreview() method
            _camRunning = false; // setting camera condition to false means stop
        }
        // condition to check whether your device have camera or not
        if (_camera != null){
            float screenAspectRatio = width / (float) height;

            Camera.Parameters parameters = _camera.getParameters();
            List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
            List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();

            int best_width = width;
            int best_height = height;

            double currentMinDistortion = MAX_ASPECT_DISTORTION;
            for (Camera.Size previewSize : supportedPreviewSizes) {
                float previewAspectRatio = (float) previewSize.width / (float) previewSize.height;
                for (Camera.Size pictureSize : supportedPictureSizes) {
                    float pictureAspectRatio = (float) pictureSize.width / (float) pictureSize.height;
                    if (Math.abs(previewAspectRatio - pictureAspectRatio) < ASPECT_RATIO_TOLERANCE) {
                        //SizePair sizePair = new SizePair(previewSize, pictureSize);
                        best_width = previewSize.width;
                        best_height = previewSize.height;
                        boolean isCandidatePortrait = previewSize.width < previewSize.height;
                        int maybeFlippedWidth = isCandidatePortrait ? previewSize.width : previewSize.height;
                        int maybeFlippedHeight = isCandidatePortrait ? previewSize.height : previewSize.width;
                        double aspectRatio = maybeFlippedWidth / (double) maybeFlippedHeight;
                        double distortion = Math.abs(aspectRatio - screenAspectRatio);
                        if (distortion < currentMinDistortion) {
                            currentMinDistortion = distortion;
                            //bestPair = sizePair;
                        }
                        break;
                    }
                }
            }
         //   parameters.getSupportedPreviewSizes();
            parameters.setPreviewSize(best_width, best_height);
            //parameters.setColorEffect(Camera.Parameters.EFFECT_SEPIA); //applying effect on camera
            _camera.setParameters(parameters); // setting camera parameters
            //_camera.setPreviewDisplay(_surfaceHolder); // setting preview of camera
            _camera.startPreview();  // starting camera preview
            holder.setFixedSize(best_width, best_height);
            _camRunning = true; // setting camera to true which means having camera
        }
        else{

        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        try {
            surfaceDestroyed(null);
            int num_of_cams = Camera.getNumberOfCameras();
            _camera = Camera.open();   // opening camera
            _camera.setDisplayOrientation(90);   // setting camera preview orientatio;
            _camera.setPreviewDisplay(holder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        if(_camera != null){
            _camera.stopPreview();  // stopping camera preview
            _camera.release();       // releasing camera
            _camera = null;          // setting camera to null when left
            _camRunning = false;   // setting camera condition to false also when exit from application
        }

    }
*/
}



