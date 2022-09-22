package com.example.btracker.DB

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

// TODO: REFLEKSJA
class TrackDB(context: Context): SQLiteOpenHelper(context, DATABASE_NAME,
    null, DATABASE_VER
) {
    companion object {
        private val DATABASE_VER    = 2
        private val DATABASE_NAME   = "TRACKLIST.db"

        // Track Table
        private val TABLE_NAME  = "Track"
        private val COL_ID      = "Id"
        private val COL_DATE    = "Date"
        private val COL_DESC    = "Description"
        private val COL_DUR     = "Duration"
        private val COL_DIST    = "Distance"
        private val COL_SPEED   = "Speed"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_QUERY =
            ("CREATE TABLE $TABLE_NAME ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COL_DATE DATE, $COL_DESC TEXT, $COL_DUR UNSIGNED BIG INT," +
                    "$COL_DIST UNSIGNED BIG INT, $COL_SPEED FLOAT)")
        db!!.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    val allTracks: List<TrackData>
        @SuppressLint("Range") @RequiresApi(Build.VERSION_CODES.O)
        get() {
            val trackList = ArrayList<TrackData>()
            val selectQuery = "SELECT * FROM $TABLE_NAME"
            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val track = TrackData(
                        date = LocalDate.parse(cursor.getString(cursor.getColumnIndex(COL_DATE))),
                        description = cursor.getString(cursor.getColumnIndex(COL_DESC)),
                        duration = cursor.getLong(cursor.getColumnIndex(COL_DUR)),
                        distance = cursor.getLong(cursor.getColumnIndex(COL_DIST)),
                        speed = cursor.getFloat(cursor.getColumnIndex(COL_SPEED))
                    )
                    trackList.add(track)
                } while (cursor.moveToNext())
                db.close()
            }
            return trackList
        }

    fun addTrack(track: TrackData) {
        val db      = this.writableDatabase
        val values  = ContentValues()

        values.put(COL_DATE, track.date.toString())
        values.put(COL_DESC, track.description)
        values.put(COL_DIST, track.distance)
        values.put(COL_DUR, track.duration)
        values.put(COL_SPEED, track.speed)

        db.insert(TABLE_NAME, null, values)
        db.close()
    }
}