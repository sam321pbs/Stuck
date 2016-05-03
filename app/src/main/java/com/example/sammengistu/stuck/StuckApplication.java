package com.example.sammengistu.stuck;

import com.firebase.client.Firebase;

import android.app.Application;


public class StuckApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
