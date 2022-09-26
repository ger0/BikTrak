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
    }

    fun retrieveSnapshot(owner: LifecycleOwner): Bitmap? {
        this.shouldSnapshot.value   = false
        this.savedSnapshot.value    = false
        savedSnapshot.removeObservers(owner)
        return bitmap.value
    }
    // scrap the data from the locationProvider
    @RequiresApi(Build.VERSION_CODES.O)
    fun scrapTrackData(): TrackData? {
        val duration = this.lastTime
        var track: TrackData?
        if (distance.value == null) {
            track = null
        } else {
            track = TrackData(
                date = LocalDate.now(),
                description = "",
                duration = duration,
                distance = distance.value!!,
                speed = if (duration > 0f) (distance.value!! / duration).toFloat() else 0f
            )
        }
        return track
    }
    fun resetTrackData() {
        initialised         = false
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