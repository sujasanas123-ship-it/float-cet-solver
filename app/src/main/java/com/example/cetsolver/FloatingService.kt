package com.example.cetsolver

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast

class FloatingService : Service() {

    private var windowManager: WindowManager? = null
    private var bubble: TextView? = null

    override fun onCreate() {
        super.onCreate()

        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return
        }

        showBubble()
    }

    private fun showBubble() {

        windowManager =
            getSystemService(WINDOW_SERVICE) as WindowManager

        bubble = TextView(this).apply {
            text = "CET"
            textSize = 13f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(45, 100, 220))
            gravity = Gravity.CENTER
            setPadding(16, 16, 16, 16)
        }

        val params = WindowManager.LayoutParams(
            90,
            90,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.END
        params.x = 20
        params.y = 300

        bubble?.setOnTouchListener(
            object : View.OnTouchListener {

                private var startX = 0
                private var startY = 0
                private var touchX = 0f
                private var touchY = 0f
                private var moved = false

                override fun onTouch(
                    view: View?,
                    event: MotionEvent
                ): Boolean {

                    when (event.action) {

                        MotionEvent.ACTION_DOWN -> {
                            startX = params.x
                            startY = params.y
                            touchX = event.rawX
                            touchY = event.rawY
                            moved = false
                            return true
                        }

                        MotionEvent.ACTION_MOVE -> {

                            val dx = event.rawX - touchX
                            val dy = event.rawY - touchY

                            if (kotlin.math.abs(dx) > 10 ||
                                kotlin.math.abs(dy) > 10) {
                                moved = true
                            }

                            params.x =
                                startX - dx.toInt()

                            params.y =
                                startY + dy.toInt()

                            try {
                                windowManager?.updateViewLayout(
                                    bubble,
                                    params
                                )
                            } catch (_: Exception) {
                            }

                            return true
                        }

                        MotionEvent.ACTION_UP -> {

                            if (!moved) {
                                Toast.makeText(
                                    this@FloatingService,
                                    "CET bubble tapped!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            return true
                        }
                    }

                    return true
                }
            }
        )

        try {
            windowManager?.addView(bubble, params)
        } catch (_: Exception) {
            bubble = null
            stopSelf()
        }
    }

    override fun onDestroy() {

        bubble?.let {
            try {
                windowManager?.removeView(it)
            } catch (_: Exception) {
            }
        }

        bubble = null
        windowManager = null

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
