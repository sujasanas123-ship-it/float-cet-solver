package com.example.cetsolver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.TextView

class FloatingService : Service() {

    private lateinit var windowManager: WindowManager
    private var bubble: TextView? = null

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        startForeground(1001, createNotification())

        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return
        }

        showBubble()
    }

    private fun showBubble() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        bubble = TextView(this).apply {
            text = "CET"
            textSize = 13f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(40, 100, 220))
            gravity = Gravity.CENTER
            setPadding(18, 18, 18, 18)

            setOnClickListener {
                // Solver action will be connected here later.
            }
        }

        val params = WindowManager.LayoutParams(
            80,
            80,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.END
        params.x = 20
        params.y = 300

        bubble?.setOnTouchListener(object : ViewTouchListener(params) {})

        windowManager.addView(bubble, params)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "cet_solver",
                "CET Solver",
                NotificationManager.IMPORTANCE_LOW
            )

            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, "cet_solver")
                .setContentTitle("Floating CET Solver")
                .setContentText("Floating button is running")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build()
        } else {
            Notification.Builder(this)
                .setContentTitle("Floating CET Solver")
                .setContentText("Floating button is running")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build()
        }
    }

    override fun onDestroy() {
        bubble?.let {
            try {
                windowManager.removeView(it)
            } catch (_: Exception) {
            }
        }
        bubble = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private open inner class ViewTouchListener(
        private val params: WindowManager.LayoutParams
    ) : android.view.View.OnTouchListener {

        private var initialX = 0
        private var initialY = 0
        private var initialTouchX = 0f
        private var initialTouchY = 0f

        override fun onTouch(
            v: android.view.View?,
            event: MotionEvent
        ): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    return true
                }

                MotionEvent.ACTION_MOVE -> {
                    params.x =
                        initialX - (event.rawX - initialTouchX).toInt()
                    params.y =
                        initialY + (event.rawY - initialTouchY).toInt()

                    windowManager.updateViewLayout(bubble, params)
                    return true
                }
            }

            return false
        }
    }
}
