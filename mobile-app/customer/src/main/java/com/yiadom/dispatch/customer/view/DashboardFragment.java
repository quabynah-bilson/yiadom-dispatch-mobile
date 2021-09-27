package com.yiadom.dispatch.customer.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.yiadom.dispatch.customer.R;
import com.yiadom.dispatch.customer.databinding.DashboardFragmentBinding;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class DashboardFragment extends Fragment {

    private DashboardFragmentBinding binding;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DashboardFragmentBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.trackOrderBtn.setOnClickListener(v -> {
            if (binding.orderId.getText().toString().isEmpty()) {
                Snackbar.make(v, "Enter an order ID first", Snackbar.LENGTH_SHORT).show();
            } else {
                // TODO: 9/26/2021 Navigate to order details page and show tracking mode
            }
        });
        binding.sendPackageContainer.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_create_order));
        binding.createOrder.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_create_order));
        try {
            binding.receivePackageContainer.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_history));
        } catch (Exception e) {
            Timber.e(e);
        }
        binding.trackOrderBtn.setOnClickListener(v -> {
            if (binding.orderId.getText().toString().isEmpty()) {
                Snackbar.make(v, "Enter an order ID first", Snackbar.LENGTH_SHORT).show();
            } else {
                // TODO: 9/26/2021 Navigate to order details page and show tracking mode
            }
        });

    }
}