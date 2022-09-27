package com.example.btracker.ui.routes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.btracker.DB.ImageData
import com.example.btracker.DB.TrackData

class RoutesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is routes Fragment"
    }
    val text: LiveData<String> = _text

    val trackList = MutableLiveData<List<TrackData>>()
    val imageList = MutableLiveData<List<ImageData>>()
}