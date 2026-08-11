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

        val info = TextView(this).apply {
            text = """
                CET Solver is ready.

                First allow the app to display over other apps.
                Then the floating solver can be enabled.
            """.trimIndent()
            textSize = 16f
            setPadding(0, 24, 0, 24)
        }

        val overlayButton = Button(this).apply {
            text = "Allow Floating Overlay"
            setOnClickListener {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    startActivity(intent)
                }
            }
        }

        val closeButton = Button(this).apply {
            text = "Close"
            setOnClickListener { finish() }
        }

        layout.addView(title)
        layout.addView(info)
        layout.addView(overlayButton)
        layout.addView(closeButton)

        setContentView(layout)
    }
}
