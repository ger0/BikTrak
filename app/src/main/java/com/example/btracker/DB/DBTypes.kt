package com.example.btracker.DB

import java.time.LocalDate
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

// data used for storing appropriate entries in sql
data class TrackData(
    var id: Int?    = null,
    var date        : LocalDate,    // date of a ride
    var description : String,       // a comment made by user (optional)
    var duration    : Long,         // total duration
    var distance    : Long,         // total distance in meters
    var speed       : Float         // average speed throughout the track
) {
}

// raw user data without any hashing
data class UserData(
    var login       : String,
    var password    : String
) {}

class ImageData(
    private var id      : Int?    = null,
    var refId           : Int,
    var type            : Int,
) {
    companion object {
        const val THUMBNAIL   = 0
        const val OTHER       = 1
    }

    lateinit var bitmap : Bitmap

    constructor(id: Int, refId: Int, type: Int, rawBytes: ByteArray?) : this(id, refId, type) {
        bitmap = BitmapFactory.decodeByteArray(rawBytes, 0, rawBytes?.size ?: 0)
    }

    val rawBytes: ByteArray
        get() {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
            return outputStream.toByteArray()
        }
}
