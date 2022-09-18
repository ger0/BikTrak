package com.example.btracker

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView

class MapsActivity : AppCompatActivity() {
    private var isTracking = false

    private lateinit var speedText       : TextView
    private lateinit var distanceText    : TextView
    private lateinit var durationChrono  : Chronometer
    private lateinit var trackButton     : Button

    private val locationProvider = LocationProvider(this)
    private val permissionManager = PermissionsManager(this, locationProvider)

    private lateinit var mapFragment: MapsFragment

    private fun toggleTracking() {
        // Stopping tracking
        if (isTracking) {
            locationProvider.stopTracking()
            trackButton.text = getString(R.string.button_toggle_tracking_on)
            trackButton.setTextColor(Color.GREEN)

            // stop the timer
            durationChrono.stop()
        }
        // Starting to track
        else {
            locationProvider.trackUser()
            trackButton.text = getString(R.string.button_toggle_tracking_off)
            trackButton.setTextColor(Color.RED)

            // start the timer
            durationChrono.base = SystemClock.elapsedRealtime()
            durationChrono.start()
        }
        mapFragment.clearTrack()
        isTracking = !isTracking
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        permissionManager.requestUserLocation()

        // figure out all elements displaying ui data
        speedText       = findViewById(R.id.speed)
        distanceText    = findViewById(R.id.distance)
        durationChrono  = findViewById(R.id.duration)
        trackButton     = findViewById(R.id.trackButton)

        // setup map fragment
        mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as MapsFragment
        mapFragment.setProvider(locationProvider)

        // setup listeners
        trackButton.setOnClickListener {
            toggleTracking()
        }
        mapFragment.ui.observe(this) { ui ->
            distanceText.text = ui.strDist
        }
    }
}