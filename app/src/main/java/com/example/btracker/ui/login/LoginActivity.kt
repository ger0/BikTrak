package com.example.btracker.ui.login

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import com.example.btracker.MainActivity
import com.example.btracker.databinding.ActivityLoginBinding

import com.example.btracker.R
import com.example.btracker.data.model.LoggedInUser

class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val usernameLogin = binding.usernameLogin
        val password = binding.password
        val signInButton = binding.signInButton
        //val loading = binding.loading
        val img = binding.imageAuthorizeIcon

        loginViewModel = ViewModelProvider(this,
            LoginViewModelFactory())[LoginViewModel::class.java]

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            signInButton.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                usernameLogin.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            //loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
                intentThis()
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })

        usernameLogin.afterTextChanged {
            loginViewModel.loginDataChanged(
                usernameLogin.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    usernameLogin.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            usernameLogin.text.toString(),
                            password.text.toString(),
                            applicationContext
                        )
                }
                false
            }
            signInButton.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                    if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        signInButton.clearFocus()
                        signInButton.isCursorVisible = false

                        val animator = ObjectAnimator.ofFloat(img, View.ROTATION,  0f,360f)
                        animator.duration = 500
                        animator.start()

                        val handler = Handler()
                        handler.postDelayed({
                            loginViewModel.login(
                                usernameLogin.text.toString(),
                                password.text.toString(),
                                applicationContext
                            )
                        }, 500)

                        return true
                    }
                    return false
                }
            })

            signInButton.setOnClickListener {
                //loading.visibility = View.VISIBLE

                val animator = ObjectAnimator.ofFloat(img, View.ROTATION,  0f,360f)
                animator.duration = 500
                animator.start()

                val handler = Handler()
                handler.postDelayed({
                    loginViewModel.login(
                        usernameLogin.text.toString(),
                        password.text.toString(),
                        applicationContext
                    )
                }, 500)
            }
        }
    }

    private fun intentThis(){
        val intent = Intent(this, MainActivity::class.java)
        val username: String = loginViewModel.loginResult.value!!.success!!.displayName
        intent.putExtra("username", username)
        startActivity(intent)
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}