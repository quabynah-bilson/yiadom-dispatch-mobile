package com.yiadom.dispatch.repository.auth;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;
import com.yiadom.dispatch.model.BaseUser;
import com.yiadom.dispatch.model.UserType;
import com.yiadom.dispatch.repository.RepositoryCallback;
import com.yiadom.dispatch.repository.user.BaseUserRepository;
import com.yiadom.dispatch.util.Utils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import timber.log.Timber;

public class AuthRepository implements BaseAuthRepository {
    private final SharedPreferences prefs;
    private final FirebaseAuth auth;
    private final BaseUserRepository userRepo;

    @Inject
    public AuthRepository(@NonNull SharedPreferences prefs, FirebaseAuth auth, BaseUserRepository userRepo) {
        this.prefs = prefs;
        this.auth = auth;
        this.userRepo = userRepo;
    }

    @Override
    public boolean isLoggedIn() {
        var userKey = prefs.getString(Utils.kPrefsUserKey, null);
        return userKey != null && !userKey.isEmpty();
    }

    @Override
    public void phoneAuth(String phoneNumber, FragmentActivity host, UserType type, @NonNull RepositoryCallback<String> callback) {
        callback.loading();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(host)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                                // This callback will be invoked in two situations:
                                // 1 - Instant verification. In some cases the phone number can be instantly
                                //     verified without needing to send or enter a verification code.
                                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                                //     detect the incoming verification SMS and perform verification without
                                //     user action.
                                Timber.d("onVerificationCompleted: %s", credential);
                                auth.signInWithCredential(credential)
                                        .addOnFailureListener(e -> callback.error(Objects.requireNonNull(e.getLocalizedMessage())))
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                FirebaseUser firebaseUser = task.getResult().getUser();
                                                if (firebaseUser != null) {
                                                    // save user data locally
                                                    prefs.edit().putString(Utils.kPrefsUserKey, firebaseUser.getUid()).apply();
                                                    prefs.edit().putString(Utils.kPrefsUserType, type.name()).apply();

                                                    // get device token
                                                    FirebaseMessaging.getInstance().getToken()
                                                            .addOnCompleteListener(runnable -> {
                                                                if (runnable.isSuccessful()) {
                                                                    var token = runnable.getResult();
                                                                    var avatar = firebaseUser.getPhotoUrl() == null ? "" : firebaseUser.getPhotoUrl().toString();
                                                                    var user = new BaseUser(
                                                                            firebaseUser.getUid(), phoneNumber,
                                                                            token, avatar, firebaseUser.getEmail());

                                                                    // save user data
                                                                    userRepo.createUser(user, type, new RepositoryCallback<String>() {
                                                                        @Override
                                                                        public void success(@Nullable String data) {
                                                                            callback.success(null);
                                                                        }

                                                                        @Override
                                                                        public void loading() {
                                                                            callback.loading();
                                                                        }

                                                                        @Override
                                                                        public void error(@NonNull String message) {
                                                                            callback.error(message);
                                                                        }
                                                                    });
                                                                }
                                                            });

                                                } else {
                                                    callback.error("No user found");
                                                }
                                            } else {
                                                callback.error(Objects.requireNonNull(Objects
                                                        .requireNonNull(task.getException())
                                                        .getLocalizedMessage()));
                                            }
                                        });
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                // This callback is invoked in an invalid request for verification is made,
                                // for instance if the the phone number format is not valid.
                                Timber.e("onVerificationFailed %s", e.getLocalizedMessage());

                                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    // Invalid request
                                    Timber.e("Invalid credentials");
                                } else if (e instanceof FirebaseTooManyRequestsException) {
                                    // The SMS quota for the project has been exceeded
                                    Timber.e("Too many requests");
                                }

                                // Show a message and update the UI
                                callback.error(Objects.requireNonNull(e.getLocalizedMessage()));
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                callback.success(s);
                            }

                            @Override
                            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                                super.onCodeAutoRetrievalTimeOut(s);
                                callback.error(s);
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    @Override
    public void googleAuth(String idToken, UserType type, @NonNull RepositoryCallback<Void> callback) {
        callback.loading();
        auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                .addOnFailureListener(e -> callback.error(Objects.requireNonNull(e.getLocalizedMessage())))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        if (firebaseUser != null) {
                            // save user data locally
                            prefs.edit().putString(Utils.kPrefsUserKey, firebaseUser.getUid()).apply();
                            prefs.edit().putString(Utils.kPrefsUserType, type.name()).apply();

                            // get device token
                            FirebaseMessaging.getInstance().getToken()
                                    .addOnCompleteListener(runnable -> {
                                        if (runnable.isSuccessful()) {
                                            var token = runnable.getResult();
                                            var avatar = firebaseUser.getPhotoUrl() == null ? "" : firebaseUser.getPhotoUrl().toString();
                                            var user = new BaseUser(
                                                    firebaseUser.getUid(), firebaseUser.getPhoneNumber(),
                                                    token, avatar, firebaseUser.getEmail());

                                            // save user data
                                            userRepo.createUser(user, type, new RepositoryCallback<String>() {
                                                @Override
                                                public void success(@Nullable String data) {
                                                    callback.success(null);
                                                }

                                                @Override
                                                public void loading() {
                                                    callback.loading();
                                                }

                                                @Override
                                                public void error(@NonNull String message) {
                                                    callback.error(message);
                                                }
                                            });
                                        }
                                    });

                        } else {
                            callback.error("No user found");
                        }
                    } else {
                        callback.error(Objects.requireNonNull(Objects
                                .requireNonNull(task.getException())
                                .getLocalizedMessage()));
                    }
                });

    }
}
