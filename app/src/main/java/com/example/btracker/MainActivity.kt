package com.example.btracker

import android.content.Context
import android.graphics.Color
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.btracker.DB.DatabaseHelper
import com.example.btracker.DB.ImageData
import com.example.btracker.DB.TrackData
import com.example.btracker.databinding.ActivityMainBinding
import com.example.btracker.ui.map.MapViewModel
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var isActive: Boolean = true

    private val mapViewModel: MapViewModel by viewModels()

    lateinit private var locationProvider:  LocationProvider
    lateinit private var permissionManager: PermissionsManager
    private var database = DatabaseHelper(this)

    private lateinit var sensorManager  : SensorManager

    // wartosci dla karty mapy
    private lateinit var durationChrono : Chronometer
    private lateinit var trackButton  : Button
    private lateinit var speedText    : TextView
    private lateinit var distanceText : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // permissions
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        locationProvider    = LocationProvider(this, mapViewModel)
        permissionManager   = PermissionsManager(this, locationProvider)
        permissionManager.requestUserLocation()

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_map, R.id.nav_routes, R.id.nav_settings
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Observers
        durationChrono = findViewById<Chronometer>(R.id.dur)
        trackButton  = findViewById<Button>(R.id.trackButt)
        speedText    = findViewById<TextView>(R.id.spd)
        distanceText = findViewById<TextView>(R.id.dist)

        if (mapViewModel.isTracking.value == true) {
            trackButton.text = getString(R.string.button_toggle_tracking_off)
            trackButton.setTextColor(Color.RED)
        }
        trackButton.setOnClickListener {
            toggleTracking(mapViewModel.isTracking.value!!)
        }
        mapViewModel.speed.observe(this, Observer { speed ->
           speedText.text = getString(R.string.speed, speed)
        })
        mapViewModel.distance.observe(this, Observer { distance ->
            distanceText.text = getString(R.string.distance, distance)
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    // ------------------------------------- Default activity -------------------------------------------
    // scrap the data from the locationProvider
    @RequiresApi(Build.VERSION_CODES.O)
    private fun scrapTrackData(): TrackData {
        return TrackData(
            date        = LocalDate.now(),
            description = "",
            duration    = timerTime,
            distance    = mapViewModel.distance.value!!,
            speed       = (mapViewModel.distance.value!! / timerTime).toFloat()
        )
    }
    // handles starting and stopping tracking functionality
    private fun toggleTracking(isTracking: Boolean) {
        // swap tracking mode
        mapViewModel.isTracking.value = !mapViewModel.isTracking.value!!
        // Stopping tracking
        if (isTracking) {
            locationProvider.stopTracking()
            // stop the timer
            stopTimer()
            // wymagania
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val trackData = scrapTrackData()
                database.addTrack(trackData)

                val imgData = ImageData(
                    refId = trackData.id!!,
                    type = ImageData.THUMBNAIL
                )

                // we want the map fragment to take snapshot of the image
                mapViewModel.shouldSnapshot.value = true
                // this observer will get removed once the image gets retrieved
                mapViewModel.savedSnapshot.observe(this, Observer{ shouldRetrieve ->
                    if (shouldRetrieve) {
                        val bitmap = mapViewModel.retrieveSnapshot(this)
                        if (bitmap != null) {
                            imgData.bitmap = bitmap
                            database.addImage(imgData)
                        }
                    }
                })
            }
            locationProvider.clearData()
            trackButton.text = getString(R.string.button_toggle_tracking_on)
            trackButton.setTextColor(Color.BLACK)
        }
        // Starting to track
        else {
            startTimer()
            locationProvider.trackUser()
            trackButton.text = getString(R.string.button_toggle_tracking_off)
            trackButton.setTextColor(Color.RED)
        }
    }

    // chronometer functions ----
    private var timerTime = Long.MIN_VALUE
    private fun startTimer(isResumed: Boolean = false) {
        val offset = if (isResumed) mapViewModel.lastTime else 0L
        durationChrono.base = SystemClock.elapsedRealtime() + offset
        durationChrono.start()
        //timerTime = Long.MIN_VALUE + offset
    }
    private fun stopTimer() {
        mapViewModel.lastTime = durationChrono.base - SystemClock.elapsedRealtime()
        durationChrono.stop()
        timerTime = durationChrono.base - SystemClock.elapsedRealtime()
    }
    // -------------------------

    override fun onStart() {
        super.onStart()
        isActive = true
        if (mapViewModel.isTracking.value == true) {
            startTimer(isResumed = true)
        }
    }

    override fun onStop() {
        super.onStop()
        stopTimer()
        Thread {
            while (!isActive) {
                Thread.sleep(LocationProvider.PERIOD_BACKGROUND)
                locationProvider.getUserLocation()
            }
        }.start()
    }
}