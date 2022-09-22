package com.example.btracker.DB

import java.time.LocalDate

// data used for storing appropriate entries in sql
data class TrackData(
    var date        : LocalDate,         // date of a ride
    var description : String,       // a comment made by user (optional)
    var duration    : Long,         // total duration
    var distance    : Long,         // total distance in meters
    var speed       : Float         // average speed throughout the track
) {}

// raw user data without any hashing
data class UserData(
    var login       : String,
    var password    : String
) {}
