package com.yiadom.dispatch.rider.view;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.yiadom.dispatch.model.UserType;
import com.yiadom.dispatch.rider.R;
import com.yiadom.dispatch.rider.databinding.ActivityRiderMainBinding;
import com.yiadom.dispatch.util.Utils;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RiderMainActivity extends AppCompatActivity {
    private ActivityRiderMainBinding binding;

    @Inject
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRiderMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // set user type
        prefs.edit().putString(Utils.kPrefsUserType, UserType.RIDER.name()).apply();

        // setup navigation fragment
        NavHostFragment hostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (hostFragment != null) {
            NavController navController = hostFragment.getNavController();
            // TODO: 9/26/2021 work with destinations
        }
    }

    @Override
    protected void onDestroy() {
        binding.unbind();
        super.onDestroy();
    }
}