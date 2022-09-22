package com.example.btracker.DB

import java.sql.Date

// data used for storing appropriate entries in sql

data class TrackData(
    val date        : Date,
    val name        : String,
    val description : String,
    val duration    : Int
) {}

// raw user data without any hashing
data class UserData(
    val login       : String,
    val password    : String
) {}
