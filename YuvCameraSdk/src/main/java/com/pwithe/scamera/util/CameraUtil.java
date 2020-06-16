package com.pwithe.scamera.util;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class CameraUtil {
    private static final String TAG = CameraUtil.class.getSimpleName();
    private static final boolean DEBUG = true;
    private static Camera mCamera = null;
    private static int mCameraID = 0;
    private static int mOrientation = 0;
    public static int SRC_VIDEO_HEIGHT = 1080;
    public static int SRC_VIDEO_WIDTH = 1920;
    private static final int SRC_PIC_HEIGHT = 1944;
    private static final int SRC_PIC_WIDTH = 2592;
    protected static CameraUtil.CameraFrameListener mListener;
    private static CameraUtil.CameraPreviewCallback mCameraPreviewCallback;
    private static SurfaceTexture mSurfaceTexture;
    private static Parameters mCameraParamters;
    private static int mResolution = 0;
    public static final int VEDIO_1080P = 0;
    public static final int VEDIO_720P = 1;
    public static final int VEDIO_480P = 2;
    private static int mIndex = 1;
    private static String mSn = "000000";
    public static final int[][] VIDEO_SIZE = new int[][]{{1920, 1080}, {1280, 720}, {848, 480}};
    private static CameraUtil.TakePictureCb mTakePictureCb;
    private static SurfaceHolder mSurfaceHolder;
    public CameraUtil() {
    }

    public static boolean openCamera(SurfaceView surfaceView,int id, final int degrees, int resolution) {
        mCameraID = id;
        mResolution = resolution;


        mSurfaceHolder = surfaceView.getHolder();
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (null == mCamera) {
                    try {
                        mCamera = Camera.open(mCameraID);
                    } catch (Exception var4) {
                        var4.printStackTrace();
                    }
                }

                startPreview();
                initCamera(degrees);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                releaseCamera();
            }
        });
        mSurfaceTexture = new SurfaceTexture(10);
        return true;
    }

    private static boolean initCamera(int degrees) {
        if (mCamera == null) {
            return false;
        } else {
            if (mResolution > 2 || mResolution < 0) {
                mResolution = 0;
            }

            SRC_VIDEO_WIDTH = VIDEO_SIZE[mResolution][0];
            SRC_VIDEO_HEIGHT = VIDEO_SIZE[mResolution][1];
            mCameraParamters = mCamera.getParameters();
            List<Size> sizes = mCameraParamters.getSupportedPreviewSizes();
            Iterator var2 = mCameraParamters.getSupportedPreviewSizes().iterator();

            Size pictureSize;
            while(var2.hasNext()) {
                pictureSize = (Size)var2.next();
                Log.d(TAG, "support preview width=" + pictureSize.width + "," + pictureSize.height);
            }

            var2 = mCameraParamters.getSupportedPictureSizes().iterator();

            while(var2.hasNext()) {
                pictureSize = (Size)var2.next();
                Log.d(TAG, "support Pictrue width=" + pictureSize.width + "," + pictureSize.height);
            }

            mCameraParamters.setPreviewFormat(ImageFormat.NV21);
            mCamera.setDisplayOrientation(degrees);
            Size previewSize = getClosestSupportedSize(mCameraParamters.getSupportedPreviewSizes(), SRC_VIDEO_WIDTH, SRC_VIDEO_HEIGHT);
            mCameraParamters.setPreviewSize(previewSize.width, previewSize.height);
            mCameraParamters.setPreviewFpsRange(30000, 30000);
            mCameraParamters.setRecordingHint(true);
            pictureSize = getClosestSupportedSize(mCameraParamters.getSupportedPictureSizes(), 2592, 1944);
            mCameraParamters.setPictureSize(pictureSize.width, pictureSize.height);
            mCamera.setParameters(mCameraParamters);
            int bufSize = previewSize.width * previewSize.height * 3 / 2;
            mCamera.addCallbackBuffer(new byte[bufSize]);
            Log.d(TAG, "getSupportedVideoSizes =" + ((Size)mCameraParamters.getSupportedVideoSizes().get(0)).width);
            Log.d(TAG, "getMaxNumDetectedFaces =" + mCameraParamters.getMaxNumDetectedFaces());
            Log.d(TAG, "getMaxNumFocusAreas =" + mCameraParamters.getMaxNumFocusAreas());
            Log.d(TAG, "getMaxNumMeteringAreas =" + mCameraParamters.getMaxNumMeteringAreas());
            Log.d(TAG, "getPictureSize =" + mCameraParamters.getPictureSize().width);
            int[] range = new int[2];
            mCameraParamters.getPreviewFpsRange(range);
            Log.d(TAG, "getPreviewFpsRange =" + Arrays.toString(range));
            Log.d(TAG, "getSupportedPreviewFormats =" + mCameraParamters.getSupportedPreviewFormats());
            Log.d(TAG, "getSupportedSceneModes =" + mCameraParamters.getSupportedSceneModes());
            mCameraPreviewCallback = new CameraUtil.CameraPreviewCallback();
            return true;
        }
    }

    public static Camera getCamera() {
        return mCamera;
    }

    public static int getResolution() {
        return mResolution;
    }

    public static void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback((PreviewCallback)null);
            mCamera.stopPreview();
            mCamera.release();
            mSurfaceTexture.release();
            mCamera = null;
        }

    }

    public static boolean isBackCamera() {
        return mCameraID == 0;
    }

    public static void startPreview() {
        if (mCamera != null) {
            try {
//                mCamera.setPreviewTexture(mSurfaceTexture);
                mCamera.setPreviewDisplay(mSurfaceHolder);

            } catch (IOException var1) {
                var1.printStackTrace();
            }

            mCamera.setPreviewCallbackWithBuffer(mCameraPreviewCallback);
            mCamera.startPreview();
            Log.d(TAG, "camera startPreview");
        }

    }

    public static void setListener(CameraUtil.CameraFrameListener listener, CameraUtil.TakePictureCb takepictureCb) {
        mListener = listener;
        mTakePictureCb = takepictureCb;
    }

    private static File getOutputMediaFile(String path) {
        File mediaFile = new File(path + ".jpg");
        return mediaFile;
    }

    public static void takePicture(String sn, int index) {
        mSn = sn;
        mIndex = index;
        mCamera.takePicture((ShutterCallback)null, (PictureCallback)null, new PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                camera.startPreview();
            }
        });
    }

    public static void savePicture(final byte[] bytes, Camera camera) {
        HandlerThread mHandlerThread = new HandlerThread("handlerThread");
        mHandlerThread.start();
        HandlerThread mTakePhotoThread = new HandlerThread("mmTakePhotoThread");
        mTakePhotoThread.start();
        Handler mTakePhotoHandler = new Handler(mTakePhotoThread.getLooper()) {
            public void handleMessage(Message msg) {
                long currentTime = System.currentTimeMillis();
                Object var4 = null;

                try {
                    String index = String.format("%5d", CameraUtil.mIndex).replace(" ", "0");
                    String dirPath = StorageUtil.getBaseFolder(1);
                    String filename = dirPath + CameraUtil.mSn + StorageUtil.getBaseTimeName() + "000_" + index + ".jpg";
                    FileOutputStream fos = new FileOutputStream(filename);
                    fos.write(bytes);
                    fos.close();
                    Log.d(CameraUtil.TAG, "onPictureTaken - wrote bytes: " + bytes.length + " to " + filename);
                } catch (Exception var9) {
                    var9.printStackTrace();
                }

                Log.d(CameraUtil.TAG, "總共時長   " + (System.currentTimeMillis() - currentTime));
            }
        };
        mTakePhotoHandler.sendEmptyMessage(1);
    }

    private static Size getClosestSupportedSize(List<Size> supportedSizes, final int requestedWidth, final int requestedHeight) {
        return (Size)Collections.min(supportedSizes, new Comparator<Size>() {
            private int diff(Size size) {
                return Math.abs(requestedWidth - size.width) + Math.abs(requestedHeight - size.height);
            }

            public int compare(Size lhs, Size rhs) {
                return this.diff(lhs) - this.diff(rhs);
            }
        });
    }

    private static boolean isSupportZoom() {
        boolean isSuppport = true;
        if (mCamera.getParameters().isSmoothZoomSupported()) {
            isSuppport = false;
        }

        return isSuppport;
    }

    public static void setZoom(boolean mode) {
        if (isSupportZoom()) {
            try {
                Parameters params = mCamera.getParameters();
                int MAX = params.getMaxZoom();
                if (MAX == 0) {
                    return;
                }

                int zoomValue = params.getZoom();
                if (mode) {
                    zoomValue += 2;
                    if (zoomValue > MAX) {
                        zoomValue = MAX;
                    }
                } else {
                    zoomValue -= 2;
                    if (zoomValue < 0) {
                        zoomValue = 0;
                    }
                }

                params.setZoom(zoomValue);
                mCamera.setParameters(params);
            } catch (Exception var4) {
                var4.printStackTrace();
            }
        } else {
            Log.e(TAG, "--------the phone not support zoom");
        }

    }

    public interface TakePictureCb {
        void takePictureFinish(String var1);
    }

    private static class SaveImageTask extends AsyncTask<byte[], Void, Void> {
        private SaveImageTask() {
        }

        protected Void doInBackground(byte[]... data) {
            FileOutputStream outStream = null;
            long var3 = System.currentTimeMillis();

            try {
                try {
                    String index = String.format("%5d", CameraUtil.mIndex).replace(" ", "0");
                    String dirPath = StorageUtil.getBaseFolder(1);
                    String filename = dirPath + CameraUtil.mSn + StorageUtil.getBaseTimeName() + "000_" + index + ".jpg";
                    File pictureFile = new File(filename);
                    outStream = new FileOutputStream(pictureFile);
                    outStream.write(data[0]);
                    outStream.flush();
                    outStream.close();
                    CameraUtil.mTakePictureCb.takePictureFinish(filename);
                } catch (FileNotFoundException var13) {
                    var13.printStackTrace();
                } catch (IOException var14) {
                    var14.printStackTrace();
                }

                return null;
            } finally {
                ;
            }
        }
    }

    public interface CameraFrameListener {
        void onPreviewYuvFrame(byte[] var1);
    }

    private static class CameraPreviewCallback implements PreviewCallback {
        private CameraPreviewCallback() {
        }

        public void onPreviewFrame(byte[] data, Camera camera) {
            CameraUtil.mListener.onPreviewYuvFrame(data);
            camera.addCallbackBuffer(data);
        }
    }
}
