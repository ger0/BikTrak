package com.example.btracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.gms.maps.GoogleMap

class MapsActivity : AppCompatActivity() {

    private lateinit var map: GoogleMap

    private val locationProvider = LocationProvider(this)
    private val permissionManager = PermissionsManager(this, locationProvider)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        permissionManager.requestUserLocation()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as MapsFragment
        mapFragment.setProvider(locationProvider)

        val trackButton = findViewById<Button>(R.id.trackButton)
        trackButton.setOnClickListener({
            locationProvider.toggleTracking()
        })
    }
}