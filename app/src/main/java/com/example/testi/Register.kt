package com.example.testi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testi.model.*
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

/**
 * Registration class for registration field
 */

class Register : AppCompatActivity() {

    private val PASSWORD_PATTERN: Pattern = Pattern.compile(
        "^" +  //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +  //any letter
                "(?=\\S+$)" +  //no white spaces
                ".{4,}" +  //at least 4 characters
                "$"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.hide()
        actionBar?.hide()

 //  Listener for Register button
        registerbutton.setOnClickListener {
            val email = emailfield.text.toString().trim()
            val name = namefield.text.toString().trim()
            val password = passwordfield.text.toString().trim()

            // Validations for email, name and password fields
            if (email.isEmpty()) {
                emailfield.error = "Enter your e-mail"
                emailfield.requestFocus()
                return@setOnClickListener
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailfield.error = "Enter a valid email address"
                return@setOnClickListener
            } else {
                emailfield.error = null
            }

            if (name.isEmpty()) {
                namefield.error = "Enter your name"
                namefield.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                passwordfield.error = "Enter your password"
                passwordfield.requestFocus()
                return@setOnClickListener
            } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
                passwordfield.error = "Password is too weak, enter better password"
                return@setOnClickListener
            } else {
                passwordfield.error = null
            }

                ApiClient.instance.createUser(UserReg(email, name, password))
                    .enqueue(object : Callback<SignUpResponse> {
                        override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                            Log.d("REGISTER", "${response.body()} responsecode: ${response.code()}")
                            if (response.isSuccessful) {
                                Toast.makeText(applicationContext,response.body().toString() ,Toast.LENGTH_LONG).show()
                                println(response.body())

                            } else {
                                Toast.makeText(applicationContext, "Error: ${response.code()}", Toast.LENGTH_LONG).show()
                            }

                            /* val gson = GsonBuilder().create()
                             val getResponse = gson.fromJson(resp, SignUpResponse::class.java)*/

                            //  MyPreferences.getInstance(applicationContext).saveJwt(response.body()?.token!!)

                            // After successful response, new intent will transition into Main page
                            // TODO, Instead of LoginActivity, go to ValidateActivity and validate the account by going to your email and getting the token
                            val intent = Intent(applicationContext, ValidateActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)

                        }
                            // Here, we catch an error and show it as a Toast
                        override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                            Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                        }

                    })

        }
        backtologin.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}