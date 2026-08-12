package com.example.cetsolver

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val title = TextView(this).apply {
            text = "Floating CET Solver"
            textSize = 24f
        }

        val message = TextView(this).apply {
            text = "Allow floating-window permission, then start the solver."
            textSize = 16f
            setPadding(0, 24, 0, 24)
        }

        val permissionButton = Button(this).apply {
            text = "Allow Floating Window"

            setOnClickListener {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    startActivity(
                        Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:$packageName")
                        )
                    )
                }
            }
        }

        val startButton = Button(this).apply {
            text = "Start Floating Button"

            setOnClickListener {
                if (Settings.canDrawOverlays(this@MainActivity)) {
                    startService(
                        Intent(
                            this@MainActivity,
                            FloatingService::class.java
                        )
                    )
                } else {
                    permissionButton.performClick()
                }
            }
        }

        val stopButton = Button(this).apply {
            text = "Stop Floating Button"

            setOnClickListener {
                stopService(
                    Intent(
                        this@MainActivity,
                        FloatingService::class.java
                    )
                )
            }
        }

        layout.addView(title)
        layout.addView(message)
        layout.addView(permissionButton)
        layout.addView(startButton)
        layout.addView(stopButton)

        setContentView(layout)
    }
}
