package com.example.btracker.ui.map

import android.content.Context
import android.graphics.Color
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.btracker.DB.*
import com.example.btracker.LocationProvider
import com.example.btracker.MapsFragment
import com.example.btracker.PermissionsManager
import com.example.btracker.R
import java.time.LocalDate

class MapsActivity : AppCompatActivity() {
    private var isTracking = false
    private val trackDB     = DatabaseHelper(this)

    private lateinit var speedText       : TextView
    private lateinit var distanceText    : TextView
    private lateinit var durationChrono  : Chronometer
    private lateinit var trackButton     : Button

    private val locationProvider    = LocationProvider(this)
    private val permissionManager   = PermissionsManager(this, locationProvider)

    private lateinit var sensorManager  : SensorManager
    private lateinit var mapFragment    : MapsFragment

    // scrap the data from the locationProvider
    @RequiresApi(Build.VERSION_CODES.O)
    private fun scrapTrackData(): TrackData? {
        val ui = mapFragment.ui.value ?: return null
        return TrackData(
            date        = LocalDate.now(),
            description = "",
            duration    = getTimerTime(),
            distance    = ui.distance,
            speed       = (ui.distance / getTimerTime()).toFloat()
        )
    }

    // chronometer functions ----
    private var timerTime = Long.MIN_VALUE
    private fun startTimer() {
        durationChrono.base = SystemClock.elapsedRealtime()
        durationChrono.start()
        timerTime = Long.MIN_VALUE
    }
    private fun stopTimer() {
        durationChrono.stop()
        timerTime = SystemClock.elapsedRealtime() - durationChrono.base
    }
    private fun getTimerTime(): Long {
        return if (timerTime == Long.MIN_VALUE) {
            SystemClock.elapsedRealtime() - durationChrono.base
        } else timerTime
    }
    // -------------------------

    // handles starting and stopping tracking functionality
    private fun toggleTracking() {
        // Stopping tracking
        if (isTracking) {
            locationProvider.stopTracking()
            // stop the timer
            stopTimer()
            // wymagania
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val trackData = scrapTrackData()
                if (trackData != null) {
                    trackDB.addTrack(trackData)
                    val img = ImageData(
                        refId = trackData.id!!,
                        type = ImageData.THUMBNAIL
                    )
                    mapFragment.writePolygonSnapshot(trackDB, img)
                }
            }
            locationProvider.clearData()
            trackButton.text = getString(R.string.button_toggle_tracking_on)
            trackButton.setTextColor(Color.GREEN)

        }
        // Starting to track
        else {
            startTimer()
            locationProvider.trackUser()
            trackButton.text = getString(R.string.button_toggle_tracking_off)
            trackButton.setTextColor(Color.RED)

            // start the timer
            mapFragment.clearTrack()
        }
        isTracking = !isTracking
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        permissionManager.requestUserLocation()

        // figure out all elements displaying ui data
        speedText       = findViewById(R.id.speed)
        distanceText    = findViewById(R.id.distance)
        durationChrono  = findViewById(R.id.duration)
        trackButton     = findViewById(R.id.trackButton)

        // setup map fragment
        mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as MapsFragment
        mapFragment.setProvider(locationProvider)
        mapFragment.setSensorManager(sensorManager)

        // setup listeners
        trackButton.setOnClickListener {
            toggleTracking()
        }
        mapFragment.ui.observe(this) { ui ->
            distanceText.text   = getString(R.string.distance, ui.distance)
            speedText.text      = getString(R.string.speed, ui.speed)
        }
    }
}