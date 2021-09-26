package com.yiadom.dispatch.view.auth;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.yiadom.dispatch.R;
import com.yiadom.dispatch.databinding.PhoneAuthFragmentBinding;
import com.yiadom.dispatch.model.UserType;
import com.yiadom.dispatch.util.Utils;
import com.yiadom.dispatch.viewmodel.AuthViewModel;
import com.yiadom.dispatch.viewmodel.ViewModelState;

import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class PhoneAuthFragment extends Fragment {
    @Inject
    SharedPreferences prefs;
    private PhoneAuthFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = PhoneAuthFragmentBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AuthViewModel authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.phoneNumber.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                handled = true;
            }
            if (handled) binding.phoneAuthBtn.performClick();
            return handled;
        });

        binding.phoneAuthBtn.setOnClickListener(v -> {
            var phoneNumber = validatePhoneNumber();
            // perform phone number authentication
            authViewModel.phoneAuthentication(phoneNumber, getActivity(),
                    UserType.valueOf(prefs.getString(Utils.kPrefsUserType, UserType.CUSTOMER.name())),
                    smsCode -> {
                        Timber.d("verification code -> %s", smsCode);
                    })
                    .observe(getViewLifecycleOwner(), authState -> {
                        binding.phoneNumberContainer.setEnabled(authState != ViewModelState.LOADING);
                        binding.phoneAuthBtn.setEnabled(authState != ViewModelState.LOADING);

                        switch (authState) {
                            case SUCCESS:
                                Snackbar.make(view, "Phone number verified successfully", Snackbar.LENGTH_LONG).show();
                                Navigation.findNavController(view).navigate(R.id.nav_dashboard);
                                break;
                            case LOADING:
                                Snackbar.make(view, "Signing in with phone number...", Snackbar.LENGTH_LONG).show();
                                break;
                            case ERROR:
                                Snackbar.make(view, "An error occurred", Snackbar.LENGTH_LONG).show();
                                break;
                            case CANCELLED:
                                Snackbar.make(view, "Check your phone number", Snackbar.LENGTH_LONG).show();
                                break;
                        }
                    });
        });
    }


    private String validatePhoneNumber() {
        String numberText = "";
        try {
            numberText = Objects.requireNonNull(binding.phoneNumber.getText()).toString();
            return numberText.startsWith("0") ? String.format(Locale.getDefault(),
                    "+233%s", numberText.substring(1)) : numberText;
        } catch (Exception e) {
            Timber.e(e);
        }
        return numberText;
    }
}