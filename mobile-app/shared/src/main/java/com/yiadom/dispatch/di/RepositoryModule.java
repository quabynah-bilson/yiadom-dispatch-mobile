package com.yiadom.dispatch.di;

import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yiadom.dispatch.repository.auth.AuthRepository;
import com.yiadom.dispatch.repository.auth.BaseAuthRepository;
import com.yiadom.dispatch.repository.user.BaseUserRepository;
import com.yiadom.dispatch.repository.user.UserRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class RepositoryModule {

    @Provides
    @Singleton
    public BaseUserRepository provideUserRepo(SharedPreferences prefs, FirebaseFirestore db) {
        return new UserRepository(prefs, db);
    }

    @Provides
    @Singleton
    public BaseAuthRepository provideAuthRepo(SharedPreferences prefs, FirebaseAuth auth, BaseUserRepository userRepo) {
        return new AuthRepository(prefs, auth, userRepo);
    }

}
