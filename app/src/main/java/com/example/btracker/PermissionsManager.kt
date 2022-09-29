package com.example.btracker

import android.Manifest
import android.location.Location
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class PermissionsManager(
    activity: AppCompatActivity
    ) {
    private var locationProvider: LocationProvider? = null
    public fun setLocationProvider(locationProvider: LocationProvider) {
        this.locationProvider = locationProvider
    }

    private val locationPermissionProvider = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { granted ->
        if (granted && locationProvider != null) {
            locationProvider!!.getUserLocation()
        }
    }

    private val storagePermissionsProvider = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
        }
    }

    fun requestUserLocation() {
        locationPermissionProvider.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        locationPermissionProvider.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    }
    fun requestWriteRead() {
        storagePermissionsProvider.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        storagePermissionsProvider.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}