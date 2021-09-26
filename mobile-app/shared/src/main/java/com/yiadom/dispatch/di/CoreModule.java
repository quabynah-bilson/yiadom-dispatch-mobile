package com.yiadom.dispatch.di;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yiadom.dispatch.util.Utils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class CoreModule {

    @Provides
    @Singleton
    public SharedPreferences providePrefs(@NonNull @ApplicationContext Context context) {
        return context.getSharedPreferences(Utils.kPrefsName, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    public FirebaseFirestore provideDB() {
        return FirebaseFirestore.getInstance();
    }

    @Provides
    @Singleton
    public FirebaseAuth provideAuth() {
        return FirebaseAuth.getInstance();
    }

}
