package com.example.btracker.ui.routes

import android.graphics.Bitmap

data class Route(
    val bitmap: Bitmap,
    val date: String,
    val duration: String,
    val distance: String,
    val averageSpeed: String,
    val username: String
    )
