package com.example.testi

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testi.model.ApiClient
import com.example.testi.model.LoginResponse
import com.example.testi.model.UserLogin
import com.example.testi.ui.parking.ParkingFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    lateinit var apiClient: ApiClient
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
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()
        actionBar?.hide()

        val myPreference = MyPreferences(this)

        val d = myPreference.getJwt()
        print(d)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val fabtext = findViewById<TextView>(R.id.fabtext)
        fab.setOnClickListener {
            if (fabtext.visibility == View.GONE){
                fabtext.visibility = View.VISIBLE;
            }
            else{
                fabtext.visibility = View.GONE
            }
        }

        fabtext.setOnClickListener {
            fabtext.visibility = View.GONE
        }

        loginbutton.setOnClickListener {

            val email = emailfield.text.toString().trim()
            val password = passwordfield.text.toString().trim()

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


            if (password.isEmpty()) {
                passwordfield.error = "Enter your password"
                passwordfield.requestFocus()
                return@setOnClickListener
            } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
                passwordfield.error = "Password length should be at least 4 letters or more, and must contain at least 1 upper and lower case letter"
                return@setOnClickListener
            } else {
                passwordfield.error = null
            }
            ApiClient.instance.loginUser(UserLogin(email, password))
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        val userN = response.body()?.username.toString()
                        val err = response.message()
                        if (response.code() == 200) {
                            Toast.makeText(
                                applicationContext,
                                "Welcome  $userN",
                                Toast.LENGTH_LONG
                            ).show()

                            val user = response.body()?.email
                            myPreference.saveUser(user.toString())

                            println(response.body()?.token)
                            MyPreferences.getInstance(applicationContext)
                                .saveJwt(response.body()?.token!!)
                            val t = MyPreferences.getInstance(applicationContext).getJwt()
                            print(t)

                            val intent = Intent(applicationContext, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                applicationContext, "Invalid email or password..   $err",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, t.localizedMessage, Toast.LENGTH_LONG).show()
                    }

                })

        }
        registerbutton.setOnClickListener {
            val intent = Intent(applicationContext, Register::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }
        forgotbutton.setOnClickListener {
            val intent = Intent(applicationContext, PasswordMod::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        /*if (MyPreferences.getInstance(this).getLoginCount() <= 0) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }*/
    }
}