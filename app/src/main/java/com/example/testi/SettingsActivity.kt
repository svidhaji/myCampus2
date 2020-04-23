package com.example.testi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_settings)

        supportActionBar?.hide()
        actionBar?.hide()

        val myPreference = MyPreferences(this)

        logoutbutton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            var jwt = myPreference.getJwt()
            jwt = null
            myPreference.destroyJwt()

        }
        val user = myPreference.getUser().toString()
        loggedas.text= "Logged in as: $user"
    }

}