package com.example.testi

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testi.model.*
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_pass.*
import kotlinx.android.synthetic.main.activity_pass.emailfield
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordMod : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass)

        supportActionBar?.hide()
        actionBar?.hide()

        resetbutton.setOnClickListener {
            val email = emailfield.text.toString().trim()

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

            ApiClient.instance.resetPass(email)
                .enqueue(object : Callback<PasswordReset> {
                    override fun onResponse(call: Call<PasswordReset>, response: Response<PasswordReset>) {
                        if (response.isSuccessful) {
                            val body = response.body()?.message
                            val gson = GsonBuilder().create()
                            val resp = gson.fromJson(body, PasswordReset::class.java)
                            // After successful response, reset token will be sent to e-mail
                            Toast.makeText(applicationContext,resp.message, Toast.LENGTH_LONG).show()
                            println(response.message().toString())

                        } else {
                            Toast.makeText(applicationContext, response.message(), Toast.LENGTH_LONG).show()
                        }
                    }
                    // Here, we catch an error and show it as a Toast
                    override fun onFailure(call: Call<PasswordReset>, t: Throwable) {
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                    }

                })
        }
        backL.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }
    }

}
