package com.example.tutorapp.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.example.tutorapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Helper {
    private static final String TAG = "Helper";

    public static String getConfigValue(Context context, String name) {
        Resources resources = context.getResources();
        try {
            InputStream rawResource = resources.openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(rawResource);
            return properties.getProperty(name);
        } catch (Resources.NotFoundException e) {
            System.out.println("Unable to find the config file: " + e.getMessage());
            Log.e(TAG, "Unable to find the config file: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Failed to open config file.");
            Log.e(TAG, "Failed to open config file.");
        }
        return null;
    }
}
