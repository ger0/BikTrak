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
import kotlin.math.roundToLong


class LocationProvider(private val activity: AppCompatActivity) {
    // constants for periodic location updates
    companion object {
        const val PERIOD_BACKGROUND: Long  = 5000
        const val PERIOD_FOREGROUND: Long  = 3000
    }

    private val client
            by lazy { LocationServices.getFusedLocationProviderClient(activity) }

    private val locations   = mutableListOf<LatLng>()
    private var distance    = 0L

    val liveLocation    = MutableLiveData<LatLng>()
    val liveLocations   = MutableLiveData<List<LatLng>>()
    val liveDistance    = MutableLiveData<Long>()
    val liveSpeed       = MutableLiveData<Int>()
    val liveBearing     = MutableLiveData<Float>()

    @SuppressLint("MissingPermission")
    fun getUserLocation() {
        client.lastLocation.addOnSuccessListener { location ->
            val latLng = LatLng(location.latitude, location.longitude)
            locations.add(latLng)
            liveLocation.value = latLng
        }
    }
    fun getRawUserLocation(): LatLng {
        return locations.last()
    }

    @SuppressLint("MissingPermission")
    fun trackUser() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        // 3 sekund interwał
        locationRequest.interval = PERIOD_FOREGROUND

        client.requestLocationUpdates(locationRequest, locationCallback,
            Looper.getMainLooper())
    }

    fun stopTracking() {
        client.removeLocationUpdates(locationCallback)
    }

    fun clearData() {
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
                distance += SphericalUtil.computeDistanceBetween(lastLoc, latLng).roundToLong()
                liveDistance.value = distance
            }
            locations.add(latLng)
            liveLocation.value  = latLng
            liveLocations.value = locations
            liveSpeed.value     = currLoc.speed.toInt()
            liveBearing.value   = currLoc.bearing
        }
    }
}