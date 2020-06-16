package com.pwithe.scamera.util;

import android.os.Environment;
import android.util.Log;
import com.pwithe.scamera.Application.CommApplication;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StorageUtil {
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static String mFilename;

    public StorageUtil() {
    }

    public static String getDirName() {
        return "JyCamera";
    }

    public static String getSDPath() {
        return Environment.getExternalStorageState().equals("mounted") ? Environment.getExternalStorageDirectory().getAbsolutePath() : Environment.getRootDirectory().getAbsolutePath();
    }

    public static String getImagePath() {
        return getPath(1);
    }

    public static String getVedioPath() {
        return getPath(2);
    }

    public static boolean checkDirExist(String path) {
        File mDir = new File(path);
        return !mDir.exists() ? mDir.mkdirs() : true;
    }

    public static String getPath(int mode) {
        String fileName = String.valueOf(System.currentTimeMillis());
        String p = getBaseFolder(mode);
        return p + fileName;
    }

    public static String getBaseFolder(int mode) {
        String baseFolder = getSDCardPath() + "/DCIM/";
        if (mode == 1) {
            baseFolder = baseFolder + "PHOTO/";
        } else {
            baseFolder = baseFolder + "VIDEO/";
        }

        File f = new File(baseFolder);
        if (!f.exists()) {
            boolean b = f.mkdirs();
            if (!b) {
                baseFolder = CommApplication.getInstance().getExternalFilesDir((String)null).getAbsolutePath() + "/";
            }
        }

        Log.d("", "smart baseFolder = " + baseFolder);
        return baseFolder;
    }

    private static String getSDCardPath() {
        String path = "";

        try {
            File file = new File("storage");
            if (file.exists()) {
                String[] list = file.list();
                if (list == null) {
                    return "";
                }

                for(int i = 0; i < list.length; ++i) {
                    if (list[i].indexOf("-") > 0) {
                        path = "/storage/" + list[i];
                        break;
                    }

                    if (!"emulated".equals(list[i]) && !"self".equals(list[i])) {
                        path = "/storage/" + list[i];
                    }
                }
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return path;
    }

    public static void setVideoFileName(String filename) {
        mFilename = filename;
    }

    public static String getVideoFileName() {
        return mFilename;
    }

    public static String getBaseTimeName() {
        String baseTimeName = (new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA)).format(new Date());
        return "_" + baseTimeName;
    }
}
