package com.yiadom.dispatch.customer.view.order;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.yiadom.dispatch.customer.databinding.FragmentDestinationLocationBinding;
import com.yiadom.dispatch.customer.view.listener.OnPageNavigationListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class DestinationLocationFragment extends Fragment {
    private OnPageNavigationListener navigationListener;
    private ActivityResultLauncher<Intent> placeSearchLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private GoogleMap googleMap;
    private FragmentDestinationLocationBinding binding;

    public DestinationLocationFragment() {
        // Required empty public constructor
    }


    @NonNull
    public static DestinationLocationFragment newInstance(OnPageNavigationListener navigationListener) {
        DestinationLocationFragment fragment = new DestinationLocationFragment();
        fragment.navigationListener = navigationListener;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDestinationLocationBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.navBack.setOnClickListener(v -> navigationListener.onNavBackPressed());
        this.placeSearchLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getData() == null) {
                        Toast.makeText(requireContext(), "No results found", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(requireContext(), "No results found", Toast.LENGTH_SHORT).show();
                    } else if (result.getResultCode() == RESULT_CANCELED) {
                        // The user canceled the operation.
                        Timber.d("User cancelled the operation");
                    }
                });

        this.requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
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
                        Toast.makeText(requireContext(), "You need to enable location permission to continue", Toast.LENGTH_SHORT).show();
                    }
                });
        binding.locationContainer.setOnClickListener(v -> {
            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(requireContext());
            placeSearchLauncher.launch(intent);
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                this.googleMap = googleMap;
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));
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
                        Timber.d("Location %.2f , %.2f", markerPosition.latitude, markerPosition.longitude);

                        // animate camera to position
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 18f));

                        // reverse geocoding
                        try {
                            var geocoder = new Geocoder(requireContext());
                            List<Address> addresses = geocoder.getFromLocation(markerPosition.latitude,
                                    markerPosition.longitude, 1);
                            Timber.d("addresses -> %s", addresses.toString());
                            binding.confirmBtn.setEnabled(!addresses.isEmpty());
                            if (addresses.isEmpty() || addresses.get(0) == null)
                                Toast.makeText(requireContext(), "No addresses found", Toast.LENGTH_SHORT).show();
                            else {
                                Address address = addresses.get(0);
                                String addressLine = address.getAddressLine(0);

                                // set address name
                                binding.location.setText(addressLine);
                                binding.confirmBtn.setOnClickListener(v ->
                                        navigationListener.onLocationSelected(markerPosition.latitude,
                                                markerPosition.longitude, true));
                            }
                        } catch (IOException e) {
                            Timber.e(e);
                        }
                    }
                });

                if (ContextCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
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
    }

    @SuppressLint("MissingPermission")
    private void getLastKnownLocation() {
        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        locationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null && googleMap != null) {
                        googleMap.clear();
                        LatLng markerPosition = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.addMarker(new MarkerOptions()
                                .draggable(true)
                                .position(markerPosition).title("Your current position"));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 16f));
                    }
                });
    }
}