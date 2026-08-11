package com.example.cetsolver

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val text = TextView(this)

        text.text = "Floating CET Solver\n\nApp started successfully!"
        text.textSize = 22f
        text.setTextColor(Color.BLACK)
        text.gravity = Gravity.CENTER
        text.setPadding(32, 32, 32, 32)

        setContentView(text)
    }
}
