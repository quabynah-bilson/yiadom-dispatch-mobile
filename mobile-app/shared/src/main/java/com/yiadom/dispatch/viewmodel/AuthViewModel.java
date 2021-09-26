package com.yiadom.dispatch.viewmodel;

import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yiadom.dispatch.model.UserType;
import com.yiadom.dispatch.repository.RepositoryCallback;
import com.yiadom.dispatch.repository.auth.BaseAuthRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AuthViewModel extends ViewModel {
    private BaseAuthRepository repository;

    @Inject
    public AuthViewModel(BaseAuthRepository repository) {
        this.repository = repository;
    }

    public boolean isLoggedIn() {
        return repository.isLoggedIn();
    }

    public LiveData<ViewModelState> phoneAuthentication(String phoneNumber, FragmentActivity host, UserType type, OnPhoneAuthListener listener) {
        var liveData = new MutableLiveData<>(ViewModelState.LOADING);

        // check phone number validity
        if (Patterns.PHONE.matcher(phoneNumber).matches()) {
            repository.phoneAuth(phoneNumber, host, type, new RepositoryCallback<>() {
                @Override
                public void success(@Nullable String data) {
                    if (data != null) listener.onRetrieveCode(data);
                    liveData.postValue(ViewModelState.SUCCESS);
                }

                @Override
                public void loading() {
                    liveData.postValue(ViewModelState.LOADING);
                }

                @Override
                public void error(@NonNull String message) {
                    liveData.postValue(ViewModelState.ERROR);
                }
            });
        } else {
            liveData.postValue(ViewModelState.CANCELLED);
        }
        return liveData;
    }

    public LiveData<ViewModelState> googleAuth(String idToken, UserType userType) {
        var liveData = new MutableLiveData<>(ViewModelState.LOADING);

        repository.googleAuth(idToken, userType, new RepositoryCallback<>() {
            @Override
            public void success(@Nullable Void data) {
                liveData.postValue(ViewModelState.SUCCESS);
            }

            @Override
            public void loading() {
                liveData.postValue(ViewModelState.LOADING);
            }

            @Override
            public void error(@NonNull String message) {
                liveData.postValue(ViewModelState.ERROR);
            }
        });
        return liveData;
    }


    public interface OnPhoneAuthListener {
        void onRetrieveCode(@NonNull String smsCode);
    }
}