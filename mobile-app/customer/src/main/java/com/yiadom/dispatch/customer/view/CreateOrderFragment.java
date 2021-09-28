package com.yiadom.dispatch.customer.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.libraries.places.api.Places;
import com.yiadom.dispatch.customer.BuildConfig;
import com.yiadom.dispatch.customer.databinding.CreateOrderFragmentBinding;
import com.yiadom.dispatch.customer.view.adapter.CreateOrderPagerAdapter;
import com.yiadom.dispatch.customer.view.listener.OnPageNavigationListener;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class CreateOrderFragment extends Fragment implements OnPageNavigationListener {
    private CreateOrderFragmentBinding binding;
    private OrderViewModel orderViewModel;

    public CreateOrderFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the SDK
        Places.initialize(requireContext(), BuildConfig.MAPS_API_KEY);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // create a translucent status bar
        try {
            requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } catch (Exception e) {
            Timber.e(e);
        }
        binding = CreateOrderFragmentBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        try {
            requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } catch (Exception e) {
            Timber.e(e);
        }
        super.onDestroy();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        orderViewModel.orderState.observe(getViewLifecycleOwner(), createOrderState -> {
            switch (createOrderState) {
                case INITIAL:
                    break;
                case HAS_PICKUP_LOCATION:
                    break;
                case HAS_DESTINATION_LOCATION:
                    break;
                case HAS_RIDER:
                    break;
                case HAS_PAID:
                    break;
            }
        });

        var pageAdapter = new CreateOrderPagerAdapter(requireActivity(), this);
        binding.pager.setAdapter(pageAdapter);
        binding.pager.setUserInputEnabled(false);
    }

    @Override
    public void onNavBackPressed() {
        binding.pager.setCurrentItem(binding.pager.getCurrentItem() - 1);
    }

    @Override
    public void onLocationSelected(double latitude, double longitude, boolean isPickupLocation) {
        binding.pager.setCurrentItem(binding.pager.getCurrentItem() + 1);
        if (isPickupLocation) orderViewModel.usePickupLocation(latitude, longitude);
        else orderViewModel.useDestinationLocation(latitude, longitude);
    }

    @Override
    public void onPackageSelected(String packageType) {
        binding.pager.setCurrentItem(binding.pager.getCurrentItem() + 1);
        orderViewModel.usePackageType(packageType);
    }

    @Override
    public void onRiderSelected(String rider) {
        binding.pager.setCurrentItem(binding.pager.getCurrentItem() + 1);
        orderViewModel.pickRider(rider);
    }

    @Override
    public void onPaymentComplete(String transactionId, double amount) {
        binding.pager.setCurrentItem(binding.pager.getCurrentItem() + 1);
        orderViewModel.makePayment(transactionId, amount);
    }

    @Override
    public void onSubmitPressed() {
        // TODO: 9/27/2021 Submit data
        Toast.makeText(requireContext(), "Submitting", Toast.LENGTH_SHORT).show();
    }

}