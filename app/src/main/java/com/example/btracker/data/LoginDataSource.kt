package com.example.btracker.data

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.btracker.DB.UserDB
import com.example.btracker.DB.UserData
import com.example.btracker.data.model.LoggedInUser
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {
    fun login(username: String, password: String, context: Context): Result<LoggedInUser> {
        try {
            val userDB = UserDB(context)
            if (userDB.checkUser(username, password)) {
                val user = LoggedInUser(java.util.UUID.randomUUID().toString(), username)
                return Result.Success(user)
            } else {
                userDB.addUser(UserData(username, password))
                val user = LoggedInUser(java.util.UUID.randomUUID().toString(), username)
                return Result.Success(user)
            }
            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
            return Result.Success(fakeUser)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}