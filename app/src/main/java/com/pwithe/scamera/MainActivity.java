package com.pwithe.scamera;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.pwithe.scamera.util.CameraUtil;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CameraUtil.startPreview();

    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceview = (SurfaceView) findViewById(R.id.surface);
        CameraUtil.setListener(mCameraFrameListener,mTakePictureCb);
        CameraUtil.openCamera(surfaceview,0,0,0);

    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraUtil.releaseCamera();
        CameraUtil.setListener(null,null);
    }
    private static final CameraUtil.CameraFrameListener mCameraFrameListener = new CameraUtil.CameraFrameListener() {
        @Override
        public void onPreviewYuvFrame(byte[] data) {
            Log.d("TAG","ddddddddddddddddddddd");

        }
    };

    private static final CameraUtil.TakePictureCb mTakePictureCb = new CameraUtil.TakePictureCb() {
        @Override
        public void takePictureFinish(String picname) {
//            Log.d(TAG,"TakePictureCb picname= " + picname);

        }
    };
}
