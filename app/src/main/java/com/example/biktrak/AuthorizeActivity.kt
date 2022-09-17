package com.example.biktrak

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class AuthorizeActivity : AppCompatActivity() {
    lateinit var inAnimation: Animation
    lateinit var outAnimation: Animation

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

        inAnimation = AnimationUtils.loadAnimation(context, R.anim.alpha_in)
        outAnimation = AnimationUtils.loadAnimation(context, R.anim.alpha_out)

        val login = findViewById<TextView>(R.id.login)
        val password = findViewById<TextView>(R.id.Password)

        val img = findViewById<ImageView>(R.id.imageAuthorizeIcon)

        val signInButton = findViewById<Button>(R.id.signInButton)
        signInButton.setOnClickListener{
            onSignInStart(login.text.toString(), password.text.toString(), img)
        }
    }

    fun onSignInStart(login: String, password: String, img:ImageView){
        val animator = ObjectAnimator.ofFloat(img, View.ROTATION,  0f,360f)
        animator.duration = 1000
        animator.start()

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