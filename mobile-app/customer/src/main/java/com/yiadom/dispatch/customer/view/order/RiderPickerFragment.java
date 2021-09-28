package com.yiadom.dispatch.customer.view.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.yiadom.dispatch.customer.databinding.FragmentRiderPickerBinding;
import com.yiadom.dispatch.customer.view.listener.OnPageNavigationListener;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RiderPickerFragment extends Fragment {
    private FragmentRiderPickerBinding binding;
    private OnPageNavigationListener navigationListener;

    public RiderPickerFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static RiderPickerFragment newInstance(OnPageNavigationListener navigationListener) {
        RiderPickerFragment fragment = new RiderPickerFragment();
        fragment.navigationListener = navigationListener;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRiderPickerBinding.inflate(inflater);
        return binding.getRoot();
    }
}