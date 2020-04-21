package com.example.testi

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testi.model.ApiClient
import com.example.testi.model.PasswordReset
import com.example.testi.model.SignUpResponse
import com.example.testi.model.UserReg
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
                            // After successful response, reset token will be sent to e-mail
                            Toast.makeText(applicationContext,"ERROR", Toast.LENGTH_LONG).show()
                            println(response.body()?.errors)

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
    }

}
