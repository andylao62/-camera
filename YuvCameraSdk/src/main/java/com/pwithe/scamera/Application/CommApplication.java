package com.pwithe.scamera.Application;

import android.app.Application;

public class CommApplication extends Application {
    public static CommApplication instance;

    public CommApplication() {
    }

    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static CommApplication getInstance() {
        return instance;
    }
}
