package com.yiadom.dispatch.customer.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.yiadom.dispatch.customer.view.listener.OnPageNavigationListener;
import com.yiadom.dispatch.customer.view.order.DestinationLocationFragment;
import com.yiadom.dispatch.customer.view.order.OrderStatusFragment;
import com.yiadom.dispatch.customer.view.order.PackagePickerFragment;
import com.yiadom.dispatch.customer.view.order.PickupLocationFragment;
import com.yiadom.dispatch.customer.view.order.RiderPickerFragment;

public class CreateOrderPagerAdapter extends FragmentStateAdapter {
    private final OnPageNavigationListener listener;

    public CreateOrderPagerAdapter(FragmentActivity host, OnPageNavigationListener listener) {
        super(host);
        this.listener = listener;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return PickupLocationFragment.newInstance(listener);
            case 1:
                return DestinationLocationFragment.newInstance(listener);
            case 2:
                return PackagePickerFragment.newInstance(listener);
            case 3:
                return RiderPickerFragment.newInstance(listener);
            default:
                return OrderStatusFragment.newInstance(listener);
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }


}
