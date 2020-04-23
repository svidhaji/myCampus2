package com.example.testi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testi.model.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.emailfield
import kotlinx.android.synthetic.main.activity_resetpass.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class ResetPassword : AppCompatActivity() {

    private val PASSWORD_PATTERN: Pattern = Pattern.compile(
        "^" +  "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +  //any letter
                "(?=\\S+$)" +  //no white spaces
                ".{4,}" +  //at least 4 characters
                "$"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resetpass)

        supportActionBar?.hide()
        actionBar?.hide()

        //  Listener for Register button
        resetPassButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passfield.text.toString().trim()
            val passwordConfirm = passconfirmfield.text.toString().trim()
            val resetToken = resettoken.text.toString().trim()

            // Validations for email, name and password fields
            if (email.isEmpty()) {
                emailField.error = "Enter your e-mail"
                emailField.requestFocus()
                return@setOnClickListener
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailField.error = "Enter a valid email address"
                return@setOnClickListener
            } else {
                emailField.error = null
            }

            if (password.isEmpty()) {
                passfield.error = "Enter your password"
                passfield.requestFocus()
                return@setOnClickListener
            } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
                passfield.error = "Password is too weak, enter better password"
                return@setOnClickListener
            } else {
                passfield.error = null
            }

            if (passwordConfirm.isEmpty()) {
                passconfirmfield.error = "You must confirm your password"
                passconfirmfield.requestFocus()
                return@setOnClickListener
            } else if (!PASSWORD_PATTERN.matcher(passwordConfirm).matches()) {
                passconfirmfield.error = "Password is too weak, enter better password"
                return@setOnClickListener
            } else if (password != passwordConfirm) {
                passconfirmfield.error = "Password doesn't match, check again!"
                return@setOnClickListener
            }
            else {
                passconfirmfield.error = null
            }

            if (resetToken.isEmpty()) {
                resettoken.error = "Enter your name"
                resettoken.requestFocus()
                return@setOnClickListener
            }

            ApiClient.instance.reset(ResetPass(email, password, passwordConfirm, resetToken))
                .enqueue(object : Callback<PasswordReset> {
                    override fun onResponse(call: Call<PasswordReset>, response: Response<PasswordReset>) {
                        Log.d("RESET", "${response.body()} responsecode: ${response.code()}")
                        if (response.isSuccessful) {
                            Toast.makeText(applicationContext,"Success: ${response.body()}", Toast.LENGTH_LONG).show()
                            println(response.body())

                        } else {
                            Toast.makeText(applicationContext, "Error: ${response.code()}", Toast.LENGTH_LONG).show()
                        }

                        /* val gson = GsonBuilder().create()
                         val getResponse = gson.fromJson(resp, SignUpResponse::class.java)*/

                        //  MyPreferences.getInstance(applicationContext).saveJwt(response.body()?.token!!)

                        // After successful response, new intent will transition into Main page
                        // TODO, Instead of LoginActivity, go to ValidateActivity and validate the account by going to your email and getting the token
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    }
                    // Here, we catch an error and show it as a Toast
                    override fun onFailure(call: Call<PasswordReset>, t: Throwable) {
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                    }

                })

        }

        backtL.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }
}