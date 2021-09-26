package com.yiadom.dispatch;

import static com.yiadom.dispatch.BuildConfig.DEBUG;

import android.app.Application;

import com.google.firebase.FirebaseApp;

import timber.log.Timber;

public class DispatchServiceApp extends Application {

    @Override
    public void onCreate() {
        // plant debug tree
        if (DEBUG) Timber.plant(new Timber.DebugTree());
        super.onCreate();

        // initialize firebase
        FirebaseApp.initializeApp(this);
    }
}
