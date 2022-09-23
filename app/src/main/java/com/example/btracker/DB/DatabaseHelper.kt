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
class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME,
    null, DATABASE_VER
) {
    companion object {
        private const val DATABASE_VER    = 2
        private const val DATABASE_NAME   = "TRACKDATA.db"

        // Track Table
        private const val TRACK_TABLE     = "Track"
        private const val TRACK_ID        = "Id"
        private const val TRACK_DATE      = "Date"
        private const val TRACK_DESC      = "Description"
        private const val TRACK_DUR       = "Duration"
        private const val TRACK_DIST      = "Distance"
        private const val TRACK_SPEED     = "Speed"

        // Image Table
        private const val IMAGE_TABLE     = "Image"
        private const val IMAGE_ID        = "Id"
        private const val IMAGE_REFID     = "TrackId"
        private const val IMAGE_TYPE      = "Type"
        private const val IMAGE_DATA      = "Data"
    }
    private var currId = 1

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TRACK =
            ("CREATE TABLE $TRACK_TABLE ($TRACK_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$TRACK_DATE DATE, $TRACK_DESC TEXT, $TRACK_DUR UNSIGNED BIG INT," +
                    "$TRACK_DIST UNSIGNED BIG INT, $TRACK_SPEED FLOAT)")
        val CREATE_IMAGE =
            ("CREATE TABLE $IMAGE_TABLE ($IMAGE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$IMAGE_REFID INT, $IMAGE_TYPE INT, $IMAGE_DATA BLOB, " +
                    "FOREIGN KEY($IMAGE_REFID) REFERENCES $TRACK_TABLE($TRACK_ID))")
        db.execSQL(CREATE_TRACK)
        db.execSQL(CREATE_IMAGE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TRACK_TABLE")
        onCreate(db)
    }

    val allTracks: List<TrackData>
        @SuppressLint("Range") @RequiresApi(Build.VERSION_CODES.O)
        get() {
            val trackList = ArrayList<TrackData>()
            val selectQuery = "SELECT * FROM $TRACK_TABLE"
            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val track = TrackData(
                        id = cursor.getInt(cursor.getColumnIndex(TRACK_ID)),
                        date = LocalDate.parse(cursor.getString(cursor.getColumnIndex(TRACK_DATE))),
                        description = cursor.getString(cursor.getColumnIndex(TRACK_DESC)),
                        duration = cursor.getLong(cursor.getColumnIndex(TRACK_DUR)),
                        distance = cursor.getLong(cursor.getColumnIndex(TRACK_DIST)),
                        speed = cursor.getFloat(cursor.getColumnIndex(TRACK_SPEED))
                    )
                    trackList.add(track)
                } while (cursor.moveToNext())
                cursor.close()
                db.close()
            }
            return trackList
        }

    val allImages: List<ImageData>
        @SuppressLint("Range") @RequiresApi(Build.VERSION_CODES.O)
        get() {
            val imgList = ArrayList<ImageData>()
            val selectQuery = "SELECT * FROM $IMAGE_TABLE"
            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val image = ImageData(
                        id = cursor.getInt(cursor.getColumnIndex(IMAGE_ID)),
                        refId = cursor.getInt(cursor.getColumnIndex(TRACK_ID)),
                        type = cursor.getInt(cursor.getColumnIndex(IMAGE_TYPE)),
                        rawBytes = cursor.getBlob(cursor.getColumnIndex(IMAGE_DATA)),
                    )
                    imgList.add(image)
                } while (cursor.moveToNext())
                cursor.close()
                db.close()
            }
            return imgList
        }

    fun addTrack(track: TrackData) {
        val db      = this.writableDatabase
        val values  = ContentValues()

        track.id = currId

        values.put(TRACK_ID, currId)
        values.put(TRACK_DATE, track.date.toString())
        values.put(TRACK_DESC, track.description)
        values.put(TRACK_DIST, track.distance)
        values.put(TRACK_DUR, track.duration)
        values.put(TRACK_SPEED, track.speed)

        db.insert(TRACK_TABLE, null, values)
        db.close()
        currId++
    }

    fun addImage(imageData: ImageData) {
        val db      = this.writableDatabase
        val values  = ContentValues()

        values.put(TRACK_ID, imageData.refId)
        values.put(IMAGE_TYPE, imageData.type)
        values.put(IMAGE_DATA, imageData.rawBytes)

        db.insert(IMAGE_TABLE, null, values)
        db.close()
    }

    fun rmTrack(id: Int) {
        val db = this.writableDatabase
        db.delete(TRACK_TABLE, "$TRACK_ID == $id", null)
    }

    fun rmImage(refId: Int) {
        val db = this.writableDatabase
        db.delete(IMAGE_TABLE, "$TRACK_ID == $refId", null)
        db.close()
    }
}