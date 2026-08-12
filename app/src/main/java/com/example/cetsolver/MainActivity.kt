package com.example.cetsolver

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    companion object {
        const val SCREEN_CAPTURE_REQUEST = 1001
    }

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
            text = "Allow floating window permission, then start the solver."
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
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    permissionButton.performClick()
                    return@setOnClickListener
                }

                val manager =
                    getSystemService(MEDIA_PROJECTION_SERVICE)
                            as MediaProjectionManager

                startActivityForResult(
                    manager.createScreenCaptureIntent(),
                    SCREEN_CAPTURE_REQUEST
                )
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

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SCREEN_CAPTURE_REQUEST &&
            resultCode == RESULT_OK &&
            data != null
        ) {
            val serviceIntent = Intent(
                this,
                FloatingService::class.java
            ).apply {
                putExtra("resultCode", resultCode)
                putExtra("data", data)
            }

            startService(serviceIntent)
        }
    }
}
