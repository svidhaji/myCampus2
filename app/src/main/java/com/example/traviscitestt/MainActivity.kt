package com.example.traviscitestt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.btn)
        var pressed = false

        button.setOnClickListener(View.OnClickListener {
            if (!pressed) {
                text.text = "Hello world"
                pressed = true
            } else if (pressed) {
                text.text = "Goodbye world"
                pressed = false
            }
        })
    }
}
