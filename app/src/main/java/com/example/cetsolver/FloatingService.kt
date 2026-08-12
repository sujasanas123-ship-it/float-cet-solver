package com.example.cetsolver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
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
    private var mediaProjection: MediaProjection? = null

    override fun onCreate() {
        super.onCreate()

        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return
        }

        startForegroundServiceNotification()
        showBubble()
    }

    private fun startForegroundServiceNotification() {
        val channelId = "cet_solver_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "CET Solver",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(channel)
        }

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, channelId)
                .setContentTitle("Floating CET Solver")
                .setContentText("Screen scanning is ready")
                .setSmallIcon(android.R.drawable.ic_menu_search)
                .build()
        } else {
            Notification.Builder(this)
                .setContentTitle("Floating CET Solver")
                .setContentText("Screen scanning is ready")
                .setSmallIcon(android.R.drawable.ic_menu_search)
                .build()
        }

        startForeground(1001, notification)
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
                                kotlin.math.abs(dy) > 10
                            ) {
                                moved = true
                            }

                            params.x = startX - dx.toInt()
                            params.y = startY + dy.toInt()

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
                                scanScreen()
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

    private fun scanScreen() {

        if (mediaProjection == null) {
            Toast.makeText(
                this,
                "Screen capture is not connected yet.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        Toast.makeText(
            this,
            "Screen scan started!",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        if (intent != null) {

            val resultCode =
                intent.getIntExtra("resultCode", -1)

            val data =
                intent.getParcelableExtra<Intent>("data")

            if (resultCode != -1 && data != null) {

                val manager =
                    getSystemService(
                        MEDIA_PROJECTION_SERVICE
                    ) as MediaProjectionManager

                mediaProjection =
                    manager.getMediaProjection(
                        resultCode,
                        data
                    )
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {

        bubble?.let {
            try {
                windowManager?.removeView(it)
            } catch (_: Exception) {
            }
        }

        mediaProjection?.stop()

        bubble = null
        windowManager = null
        mediaProjection = null

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
