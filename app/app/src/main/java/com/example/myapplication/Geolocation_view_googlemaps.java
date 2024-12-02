package com.example.myapplication;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.myapplication.databinding.ActivityGeolocationViewGooglemapsBinding;

/**
 * Author: Jin
 * Edited: Erin-Marie
 * Class for displaying an events geolocation map
 */
public class Geolocation_view_googlemaps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityGeolocationViewGooglemapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGeolocationViewGooglemapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // get latitude and longitude through getIntent
        double latitude = getIntent().getDoubleExtra("latitude", 53.0); // default if not passed
        double longitude = getIntent().getDoubleExtra("longitude", -113.0); // default if not passed

        // LatLng object for the event location
        LatLng eventLocation = new LatLng(latitude, longitude);

        // default zoom level
        float zoomLevel = 10.0f;

        // marker for the event location
        mMap.addMarker(new MarkerOptions().position(eventLocation).title("Event Location"));

        // move camera / zoom in
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, zoomLevel));
    }
}