package com.yiadom.dispatch.customer.view.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.yiadom.dispatch.customer.databinding.FragmentOrderStatusBinding;
import com.yiadom.dispatch.customer.view.listener.OnPageNavigationListener;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrderStatusFragment extends Fragment {
    private FragmentOrderStatusBinding binding;
    private OnPageNavigationListener navigationListener;

    public OrderStatusFragment() {
        // Required empty public constructor
    }


    @NonNull
    public static OrderStatusFragment newInstance(OnPageNavigationListener navigationListener) {
        OrderStatusFragment fragment = new OrderStatusFragment();
        fragment.navigationListener = navigationListener;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrderStatusBinding.inflate(inflater);
        return binding.getRoot();
    }
}