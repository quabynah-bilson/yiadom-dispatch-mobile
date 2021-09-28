package com.yiadom.dispatch.customer.view.order;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.yiadom.dispatch.customer.databinding.FragmentPackagePickerBinding;
import com.yiadom.dispatch.customer.view.adapter.OrderPackageListAdapter;
import com.yiadom.dispatch.customer.view.listener.OnPackageSelectedListener;
import com.yiadom.dispatch.customer.view.listener.OnPageNavigationListener;
import com.yiadom.dispatch.model.OrderPackageType;

import java.util.Arrays;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PackagePickerFragment extends Fragment implements OnPackageSelectedListener {
    private FragmentPackagePickerBinding binding;
    private OnPageNavigationListener navigationListener;
    private OrderPackageListAdapter packageListAdapter;

    public PackagePickerFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static PackagePickerFragment newInstance(OnPageNavigationListener navigationListener) {
        PackagePickerFragment fragment = new PackagePickerFragment();
        fragment.navigationListener = navigationListener;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPackagePickerBinding.inflate(inflater);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        packageListAdapter = new OrderPackageListAdapter(this);
        binding.orderPackageList.setAdapter(packageListAdapter);
            packageListAdapter.submitList(Arrays.stream(OrderPackageType.values()).collect(Collectors.toList()));
    }

    @Override
    public void onPackageSelected(OrderPackageType type) {
        packageListAdapter.toggleItem(type);
    }
}