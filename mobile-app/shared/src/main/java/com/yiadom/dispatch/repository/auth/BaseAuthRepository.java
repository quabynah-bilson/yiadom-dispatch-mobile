package com.yiadom.dispatch.repository.auth;

import androidx.fragment.app.FragmentActivity;

import com.google.firebase.firestore.auth.User;
import com.yiadom.dispatch.model.UserType;
import com.yiadom.dispatch.repository.RepositoryCallback;

public interface BaseAuthRepository {

    boolean isLoggedIn();

    void phoneAuth(String phoneNumber, FragmentActivity host, UserType type, RepositoryCallback<String> callback);

    void googleAuth(String idToken, UserType type, RepositoryCallback<Void> callback);
}
