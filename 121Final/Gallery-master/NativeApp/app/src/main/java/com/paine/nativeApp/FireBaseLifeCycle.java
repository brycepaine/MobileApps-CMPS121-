package com.paine.nativeApp;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by Ashu on 24/11/15.
 */
public class FireBaseLifeCycle extends Application {

    private static final String TAG = "AwesomeFireBase";


    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}