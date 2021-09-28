package com.yiadom.dispatch.customer.view;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.GeoPoint;

public class OrderViewModel extends ViewModel {
    private final MutableLiveData<CreateOrderState> _orderState = new MutableLiveData<>(CreateOrderState.INITIAL);
    LiveData<CreateOrderState> orderState = _orderState;
    @Nullable
    private GeoPoint _pickupLocation;
    @Nullable
    private GeoPoint _destinationLocation;
    @Nullable
    private String _rider;
    @Nullable
    private String _packageType;

    void usePickupLocation(double latitude, double longitude) {
        _orderState.postValue(CreateOrderState.HAS_PICKUP_LOCATION);
        this._pickupLocation = new GeoPoint(latitude, longitude);
    }

    void useDestinationLocation(double latitude, double longitude) {
        _orderState.postValue(CreateOrderState.HAS_DESTINATION_LOCATION);
        this._destinationLocation = new GeoPoint(latitude, longitude);
    }

    void pickRider(String rider) {
        _orderState.postValue(CreateOrderState.HAS_RIDER);
        this._rider = rider;
    }

    void makePayment(String transactionId, double amount) {
        _orderState.postValue(CreateOrderState.HAS_PAID);
        // TODO: 9/27/2021 Make payment
    }

    void usePackageType(String packageType) {
        _orderState.postValue(CreateOrderState.HAS_PACKAGE_TYPE);
        this._packageType = packageType;
    }

    public enum CreateOrderState {
        INITIAL,
        HAS_PICKUP_LOCATION,
        HAS_DESTINATION_LOCATION,
        HAS_PACKAGE_TYPE,
        HAS_RIDER,
        HAS_PAID
    }
}