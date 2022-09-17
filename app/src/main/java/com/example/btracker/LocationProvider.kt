package com.example.btracker

import android.annotation.SuppressLint
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import kotlin.math.roundToInt

class LocationProvider(private val activity: AppCompatActivity) {
    private val client
            by lazy { LocationServices.getFusedLocationProviderClient(activity) }

    private val locations   = mutableListOf<LatLng>()
    private var distance    = 0

    val liveLocation = MutableLiveData<LatLng>()
    val liveLocations = MutableLiveData<List<LatLng>>()
    val liveDistance = MutableLiveData<Int>()

    @SuppressLint("MissingPermission")
    fun getUserLocation() {
        client.lastLocation.addOnSuccessListener { location ->
            val latLng = LatLng(location.latitude, location.longitude)
            locations.add(latLng)
            liveLocation.value = latLng
        }
    }

    @SuppressLint("MissingPermission")
    fun trackUser() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 3000

        client.requestLocationUpdates(locationRequest, locationCallback,
            Looper.getMainLooper())
    }

    fun stopTracking() {
        client.removeLocationUpdates(locationCallback)
        locations.clear()
        distance = 0
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            val currLoc = p0.lastLocation
            val latLng  = LatLng(currLoc.latitude, currLoc.longitude)

            val lastLoc = locations.lastOrNull()
            if (lastLoc != null) {
                distance += SphericalUtil.computeDistanceBetween(lastLoc, latLng).roundToInt()
                liveDistance.value = distance
            }
            locations.add(latLng)
            liveLocations.value = locations
        }
    }
}