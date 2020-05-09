package com.example.testi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testi.model.ApiClient
import com.example.testi.model.LoginResponse
import com.example.testi.model.Validate
import kotlinx.android.synthetic.main.activity_validate.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ValidateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validate)

        supportActionBar?.hide()
        actionBar?.hide()

        //  Listener for Register button
        validatebutton.setOnClickListener {
            val email = emailfield.text.toString().trim()
            val token = tokenfield.text.toString().trim()

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

            if (token.isEmpty()) {
                tokenfield.error = "Enter token"
                tokenfield.requestFocus()
                return@setOnClickListener
            }

            ApiClient.instance.validate(Validate(email, token))
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        Log.d("REGISTER", "${response.body()} responsecode: ${response.code()}")
                        if (response.code() == 200) {
                            MyPreferences.getInstance(applicationContext).saveJwt(response.body()?.token!!)
                            Toast.makeText(applicationContext,"Welcome to myCampus: ${response.body()?.username}!",Toast.LENGTH_LONG).show()
                            println(response.body())

                        } else {
                            Toast.makeText(applicationContext, response.message(), Toast.LENGTH_LONG).show()
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
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                    }

                })

        }
    }
}