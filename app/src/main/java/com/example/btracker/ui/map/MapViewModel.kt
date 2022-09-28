package com.example.btracker.ui.map

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.btracker.DB.TrackData
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDate

class MapViewModel : ViewModel() {
    var lastTime    : Long = 0L

    // should the map fragment take a snapshot?
    val shouldSnapshot  = MutableLiveData(false)
    // should main activity save the snapshot to a database?
    val savedSnapshot   = MutableLiveData(false)

    var initialised = false
    val isTracking  = MutableLiveData(false)
    val speed       = MutableLiveData<Int>()
    val distance    = MutableLiveData<Long>()
    val position    = MutableLiveData<LatLng>()
    val bearing     = MutableLiveData<Float>()
    val path        = MutableLiveData<MutableList<LatLng>>()

    private val bitmap      = MutableLiveData<Bitmap>()

    fun saveSnapshot(bitmap: Bitmap?) {
        this.bitmap.value = bitmap
        this.savedSnapshot.value = true
        this.shouldSnapshot.value = false
    }

    fun retrieveSnapshot(owner: LifecycleOwner): Bitmap? {
        savedSnapshot.removeObservers(owner)
        this.savedSnapshot.value    = false
        return this.bitmap.value
    }
    // scrap the data from the locationProvider
    @RequiresApi(Build.VERSION_CODES.O)
    fun scrapTrackData(username: String): TrackData? {
        val duration = this.lastTime
        val track: TrackData?
        if (distance.value == null) {
            track = null
        } else {
            // in seconds instead of milliseconds
            val calcDuration = duration / 1000
            track = TrackData(
                date = LocalDate.now(),
                username = username,
                duration = calcDuration,
                distance = distance.value!!,
                speed = if (calcDuration > 0f) (distance.value!! / calcDuration).toFloat() else 0f
            )
        }
        return track
    }
    fun resetTrackData() {
        speed.value         = 0
        distance.value      = 0
        if (path.value != null) {
            path.value!!.clear()
        }
    }

    fun startTracking() {
        initialised = true
        isTracking.value = true
    }
}