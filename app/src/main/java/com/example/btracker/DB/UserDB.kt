package com.example.btracker.DB

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi

class UserDB(context: Context?): SQLiteOpenHelper(context, DATABASE_NAME,
    null, DATABASE_VER
) {
    companion object {
        private val DATABASE_VER    = 2
        private val DATABASE_NAME   = "USERLIST.db"

        // Userlist
        private val TABLE_NAME  = "User"
        private val COL_LOGIN   = "Login"
        private val COL_PASSWD  = "Password"
    }

    @SuppressLint("SQLiteString")
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_QUERY =
            ("CREATE TABLE $TABLE_NAME ($COL_LOGIN STRING PRIMARY KEY, $COL_PASSWD STRING)")
        db!!.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addUser(user: UserData) {
        val db      = this.writableDatabase
        val values  = ContentValues()

        values.put(COL_LOGIN, user.login)
        values.put(COL_PASSWD, user.password)

        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    val allUserData: ArrayList<UserData>
        @SuppressLint("Range") @RequiresApi(Build.VERSION_CODES.O)
        get() {
            val userList = ArrayList<UserData>()
            val selectQuery = "SELECT * FROM $TABLE_NAME"
            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val user = UserData(
                        login = cursor.getString(cursor.getColumnIndex(COL_LOGIN)),
                        password = cursor.getString(cursor.getColumnIndex(COL_PASSWD))
                    )
                    userList.add(user)
                } while (cursor.moveToNext())
                cursor.close()
                db.close()
            }
            return userList
        }

    @SuppressLint("Range")
    fun checkUser(login: String, password: String): Boolean {
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COL_LOGIN = '$login'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()){
            if (password == cursor.getString(cursor.getColumnIndex(COL_PASSWD))) {
                return true
            }
        }
        cursor.close()
        return false
    }
}