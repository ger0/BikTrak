package com.example.btracker.ui.map

import android.graphics.Bitmap
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng

class MapViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    // should the map fragment take a snapshot?
    val shouldSnapshot  = MutableLiveData<Boolean>(false)
    // should main activity save the snapshot to a database?
    val savedSnapshot   = MutableLiveData<Boolean>(false)

    val isTracking  = MutableLiveData<Boolean>(false)
    val speed       = MutableLiveData<Int>()
    val distance    = MutableLiveData<Long>()
    val position    = MutableLiveData<LatLng>()
    val bearing     = MutableLiveData<Float>()
    val path        = MutableLiveData<List<LatLng>>()

    val bitmap      = MutableLiveData<Bitmap>()

    fun saveSnapshot(bitmap: Bitmap?) {
        this.bitmap.value = bitmap
        this.savedSnapshot.value    = true
    }

    fun retrieveSnapshot(owner: LifecycleOwner): Bitmap? {
        this.shouldSnapshot.value   = false
        this.savedSnapshot.value    = false
        savedSnapshot.removeObservers(owner)
        return bitmap.value
    }
}