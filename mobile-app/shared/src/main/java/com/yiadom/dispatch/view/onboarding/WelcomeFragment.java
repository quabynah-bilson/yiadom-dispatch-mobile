package com.yiadom.dispatch.view.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.yiadom.dispatch.R;
import com.yiadom.dispatch.databinding.WelcomeFragmentBinding;
import com.yiadom.dispatch.viewmodel.AuthViewModel;
import com.yiadom.dispatch.viewmodel.UserOnboardingViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WelcomeFragment extends Fragment {

    private WelcomeFragmentBinding binding;
    private UserOnboardingViewModel onboardingViewModel;
    private AuthViewModel authViewModel;

    public WelcomeFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = WelcomeFragmentBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onboardingViewModel = new ViewModelProvider(this).get(UserOnboardingViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.getStartedBtn.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(authViewModel.isLoggedIn()
                        ? R.id.nav_dashboard : R.id.nav_login));

    }
}