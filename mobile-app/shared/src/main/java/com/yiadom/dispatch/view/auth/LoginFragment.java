package com.yiadom.dispatch.view.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.yiadom.dispatch.R;
import com.yiadom.dispatch.databinding.LoginFragmentBinding;
import com.yiadom.dispatch.model.UserType;
import com.yiadom.dispatch.util.Utils;
import com.yiadom.dispatch.viewmodel.AuthViewModel;
import com.yiadom.dispatch.viewmodel.ViewModelState;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class LoginFragment extends Fragment {

    @Inject
    SharedPreferences prefs;
    private AuthViewModel authViewModel;
    private LoginFragmentBinding binding;
    private final ActivityResultLauncher<Intent> googleAuthInvoker = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    UserType userType = UserType.valueOf(prefs.getString(Utils.kPrefsUserType, UserType.CUSTOMER.name()));
                    authViewModel.googleAuth(account.getIdToken(), userType)
                            .observe(getViewLifecycleOwner(), authState -> {
                                binding.googleAuth.setEnabled(authState != ViewModelState.LOADING);
                                binding.phoneAuth.setEnabled(authState != ViewModelState.LOADING);

                                switch (authState) {
                                    case SUCCESS:
                                        Snackbar.make(binding.getRoot(), "Signed in successfully", Snackbar.LENGTH_LONG).show();
                                        Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_dashboard);
                                        break;
                                    case LOADING:
                                        Snackbar.make(binding.getRoot(), "Signing in with google...", Snackbar.LENGTH_LONG).show();
                                        break;
                                    case ERROR:
                                        Snackbar.make(binding.getRoot(), "An error occurred", Snackbar.LENGTH_LONG).show();
                                        break;
                                    case CANCELLED:
                                        Snackbar.make(binding.getRoot(), "Check your phone number", Snackbar.LENGTH_LONG).show();
                                        break;
                                }
                            });
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Timber.e("Google sign in failed -> %s", e.getLocalizedMessage());
                }
            });

    public LoginFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = LoginFragmentBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // google auth
        binding.googleAuth.setOnClickListener(v -> {
            // Configure Google Sign In
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            var signInClient = GoogleSignIn.getClient(requireActivity(), gso);
            googleAuthInvoker.launch(signInClient.getSignInIntent());
        });

        // phone auth
        binding.phoneAuth.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.nav_phone_auth));
    }
}