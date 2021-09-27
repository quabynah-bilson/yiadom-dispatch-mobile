package com.yiadom.dispatch.customer.view.listener;

public interface OnPageNavigationListener {
    void onNavBackPressed();

    void onLocationSelected(double latitude, double longitude, boolean isPickupLocation);

    void onSubmitPressed();
}
