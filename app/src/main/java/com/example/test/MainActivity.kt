package com.example.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find the button by its ID
        val startButton = findViewById<Button>(R.id.map)
        val signalButton = findViewById<Button>(R.id.signal)
        // Set an OnClickListener to the button
        startButton.setOnClickListener {
            // Create an Intent to start MapActivity
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
        signalButton.setOnClickListener {
            // Create an Intent to start MapActivity
            val intent = Intent(this, SignalActivity::class.java)
            startActivity(intent)
        }
    }



}