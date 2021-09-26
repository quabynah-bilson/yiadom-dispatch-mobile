package com.yiadom.dispatch.repository.user;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.yiadom.dispatch.model.BaseUser;
import com.yiadom.dispatch.model.UserType;
import com.yiadom.dispatch.repository.RepositoryCallback;
import com.yiadom.dispatch.util.Utils;

import java.util.Objects;

import javax.inject.Inject;

public class UserRepository implements BaseUserRepository {
    private final SharedPreferences prefs;
    private final FirebaseFirestore db;

    @Inject
    public UserRepository(@NonNull SharedPreferences prefs, FirebaseFirestore db) {
        this.prefs = prefs;
        this.db = db;
    }

    @Override
    public void createUser(@NonNull BaseUser user, UserType type, @NonNull RepositoryCallback<String> callback) {
        callback.loading();

        db.collection(type == UserType.CUSTOMER ? Utils.kCustomersRef : Utils.kRidersRef)
                .document(user.id)
                .set(user, SetOptions.merge())
                .addOnFailureListener(e -> callback.error(Objects.requireNonNull(e.getLocalizedMessage())))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.success(user.deviceToken);
                    } else
                        callback.error(Objects.requireNonNull(Objects
                                .requireNonNull(task.getException()).getLocalizedMessage()));
                });
    }
}
