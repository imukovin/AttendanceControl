package com.example.tutorapp.helpers;

import android.app.Application;

public class TheApplication extends Application {
    private static TheApplication sApplication;

    public static TheApplication getInstance() {
        return sApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
    }
}
