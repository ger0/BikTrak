package com.example.biktrak

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class AuthorizeActivity : AppCompatActivity() {

    val context = this

    var isRunMainActivity = false
    val threadMainActivity = Thread(){
        runOnUiThread(){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorize)

        val login = findViewById<TextView>(R.id.login)
        val password = findViewById<TextView>(R.id.Password)

        val signInButton = findViewById<Button>(R.id.signInButton)
        signInButton.setOnClickListener{
            onSignInStart(login.text.toString(), password.text.toString())
        }
    }

    fun onSignInStart(login: String, password: String){
        if (!isPass(login, password)){
            return
        }
        startMain()
    }

    fun isPass(login: String, password: String):Boolean{
        if (login == ""){
            Toast.makeText(context, "Fill login field!", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(password==""){
            Toast.makeText(context, "Fill password field!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    fun startMain(){
        if(!isRunMainActivity){
            threadMainActivity.start()
            isRunMainActivity = true
        }
        else{
            threadMainActivity.run()
        }
        onPause()
    }
}