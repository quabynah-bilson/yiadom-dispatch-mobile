package com.yiadom.dispatch.customer.view.adapter;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.yiadom.dispatch.customer.R;
import com.yiadom.dispatch.customer.databinding.PageDestinationLocationBinding;
import com.yiadom.dispatch.customer.databinding.PagePickupLocationBinding;
import com.yiadom.dispatch.customer.view.listener.OnPageNavigationListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class CreateOrderPagerAdapter extends PagerAdapter {
    private final Fragment host;
    private final OnPageNavigationListener listener;
    private final ActivityResultLauncher<Intent> placeSearchLauncher;
    private final ActivityResultLauncher<String> requestPermissionLauncher;
    private final Geocoder geocoder;
    @Nullable
    private GoogleMap googleMap;

    @SuppressLint("MissingPermission")
    public CreateOrderPagerAdapter(@NonNull Fragment host, OnPageNavigationListener listener) {
        this.host = host;
        this.listener = listener;
        this.geocoder = new Geocoder(host.requireContext());
        this.placeSearchLauncher = host.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getData() == null) {
                        Toast.makeText(host.requireContext(), "No results found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (result.getResultCode() == RESULT_OK && googleMap != null) {
                        Place place = Autocomplete.getPlaceFromIntent(result.getData());
                        Timber.d("Place: %s [%s]", place.getName(), place.getId());
                        LatLng position = place.getLatLng();
                        if (position != null) {
                            googleMap.clear();
                            googleMap.addMarker(new MarkerOptions()
                                    .position(position)
                                    .draggable(true));
                        }
                    } else if (result.getResultCode() == AutocompleteActivity.RESULT_ERROR) {
                        // TODO: Handle the error.
                        Status status = Autocomplete.getStatusFromIntent(result.getData());
                        Timber.d("Status message -> %s", status.getStatusMessage());
                        Toast.makeText(host.requireContext(), "No results found", Toast.LENGTH_SHORT).show();
                    } else if (result.getResultCode() == RESULT_CANCELED) {
                        // The user canceled the operation.
                        Timber.d("User cancelled the operation");
                    }
                });

        this.requestPermissionLauncher =
                host.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        // Permission is granted. Continue the action or workflow in your
                        // app.
                        getLastKnownLocation();
                    } else {
                        // Explain to the user that the feature is unavailable because the
                        // features requires a permission that the user has denied. At the
                        // same time, respect the user's decision. Don't link to system
                        // settings in an effort to convince the user to change their
                        // decision.
                        Toast.makeText(host.requireContext(), "You need to enable location permission to continue", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        var v = getItem(position);
        container.addView(v);
        return v;
    }

    @NonNull
    @SuppressLint("MissingPermission")
    private View getItem(int position) {
        switch (position) {
            case 0:
                PagePickupLocationBinding pickupLocationBinding = PagePickupLocationBinding.inflate(host.getLayoutInflater());
                pickupLocationBinding.locationContainer.setOnClickListener(v -> {
                    // Set the fields to specify which types of place data to
                    // return after the user has made a selection.
                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

                    // Start the autocomplete intent.
                    Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                            .build(host.requireContext());
                    placeSearchLauncher.launch(intent);
                });
                pickupLocationBinding.navBack.setOnClickListener(v -> listener.onNavBackPressed());

                SupportMapFragment mapFragment = (SupportMapFragment) host.getChildFragmentManager()
                        .findFragmentById(R.id.map);
                if (mapFragment != null) {
                    mapFragment.getMapAsync(googleMap -> {
                        this.googleMap = googleMap;
                        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(host.requireContext(), R.raw.map_style));
                        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                            @Override
                            public void onMarkerDragStart(@NonNull Marker marker) {

                            }

                            @Override
                            public void onMarkerDrag(@NonNull Marker marker) {

                            }

                            @Override
                            public void onMarkerDragEnd(@NonNull Marker marker) {
                                LatLng markerPosition = marker.getPosition();

                                // animate camera to position
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 18f));

                                // reverse geocoding
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(markerPosition.latitude,
                                            markerPosition.longitude, 1);
                                    pickupLocationBinding.confirmBtn.setEnabled(!addresses.isEmpty());
                                    if (addresses.isEmpty() || addresses.get(0) == null)
                                        Toast.makeText(host.requireContext(), "No addresses found", Toast.LENGTH_SHORT).show();
                                    else {
                                        Address address = addresses.get(0);
                                        String addressLine = address.getAddressLine(0);

                                        // set address name
                                        pickupLocationBinding.location.setText(addressLine);
                                        pickupLocationBinding.confirmBtn.setOnClickListener(v ->
                                                listener.onLocationSelected(markerPosition.latitude,
                                                        markerPosition.longitude, true));
                                    }
                                } catch (IOException e) {
                                    Timber.e(e);
                                }
                            }
                        });

                        if (ContextCompat.checkSelfPermission(
                                host.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED) {
                            getLastKnownLocation();
                        } else {
                            // You can directly ask for the permission.
                            // The registered ActivityResultCallback gets the result of this request.
                            requestPermissionLauncher.launch(
                                    Manifest.permission.ACCESS_COARSE_LOCATION);
                        }
                    });
                }
                return pickupLocationBinding.getRoot();
            default:
                PageDestinationLocationBinding destinationLocationBinding = PageDestinationLocationBinding.inflate(host.getLayoutInflater());
                destinationLocationBinding.locationContainer.setOnClickListener(v -> {
                    // Set the fields to specify which types of place data to
                    // return after the user has made a selection.
                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

                    // Start the autocomplete intent.
                    Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                            .build(host.requireContext());
                    placeSearchLauncher.launch(intent);
                });
                destinationLocationBinding.navBack.setOnClickListener(v -> listener.onNavBackPressed());

                SupportMapFragment map = (SupportMapFragment) host.getChildFragmentManager()
                        .findFragmentById(R.id.map);
                if (map != null) {
                    map.getMapAsync(gMap -> {
                        this.googleMap = gMap;
                        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(host.requireContext(), R.raw.map_style));
                        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                            @Override
                            public void onMarkerDragStart(@NonNull Marker marker) {

                            }

                            @Override
                            public void onMarkerDrag(@NonNull Marker marker) {

                            }

                            @Override
                            public void onMarkerDragEnd(@NonNull Marker marker) {
                                LatLng markerPosition = marker.getPosition();

                                // animate camera to position
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 18f));

                                // reverse geocoding
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(markerPosition.latitude,
                                            markerPosition.longitude, 1);
                                    destinationLocationBinding.confirmBtn.setEnabled(!addresses.isEmpty());
                                    if (addresses.isEmpty() || addresses.get(0) == null)
                                        Toast.makeText(host.requireContext(), "No addresses found", Toast.LENGTH_SHORT).show();
                                    else {
                                        Address address = addresses.get(0);
                                        String addressLine = address.getAddressLine(0);

                                        // set address name
                                        destinationLocationBinding.location.setText(addressLine);
                                        destinationLocationBinding.confirmBtn.setOnClickListener(v ->
                                                listener.onLocationSelected(markerPosition.latitude,
                                                        markerPosition.longitude, false));
                                    }
                                } catch (IOException e) {
                                    Timber.e(e);
                                }
                            }
                        });

                        if (ContextCompat.checkSelfPermission(
                                host.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED) {
                            getLastKnownLocation();
                        } else {
                            // You can directly ask for the permission.
                            // The registered ActivityResultCallback gets the result of this request.
                            requestPermissionLauncher.launch(
                                    Manifest.permission.ACCESS_COARSE_LOCATION);
                        }
                    });
                }
                return destinationLocationBinding.getRoot();
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    private void getLastKnownLocation() {
        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(host.requireActivity());
        locationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null && googleMap != null) {
                       /* googleMap.setMyLocationEnabled(true);
                        googleMap.setOnMyLocationButtonClickListener(() -> {
                            // Logic to handle location object
                            // Add a marker in Sydney and move the camera
                            LatLng markerPosition = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.addMarker(new MarkerOptions()
                                    .draggable(true)
                                    .position(markerPosition).title("Your current position"));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 16f));
                            return true;
                        });*/
                        googleMap.clear();
                        // Logic to handle location object
                        // Add a marker in Sydney and move the camera
                        LatLng markerPosition = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.addMarker(new MarkerOptions()
                                .draggable(true)
                                .position(markerPosition).title("Your current position"));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 16f));
                    }
                });
    }
}
