package com.example.cetsolver

import android.app.Service
import android.content.Intent
import android.os.IBinder

class FloatingService : Service() {

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
