package com.example.btracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.btracker.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.PolylineOptions
import java.lang.System.currentTimeMillis

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val locationProvider = LocationProvider(this)
    private val permissionManager = PermissionsManager(this, locationProvider)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        currentTimeMillis()

        locationProvider.liveLocation.observe(this) {
            latLng -> map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
        }
        permissionManager.requestUserLocation()
        map.setMyLocationEnabled(true);
        map.uiSettings.isZoomControlsEnabled = true
        /*
        val polyline1 = googleMap.addPolyline(
            PolylineOptions()
            .clickable(true)
            .add(
                LatLng(0.016, 143.321),
                LatLng(69.747, 145.592),
                LatLng(34.364, 100.891),
                LatLng(-33.501, 50.217),
                LatLng(-32.306, 49.248),
                LatLng(-32.491, 147.309)))
        polyline1.tag = "A"
         */
    }
}