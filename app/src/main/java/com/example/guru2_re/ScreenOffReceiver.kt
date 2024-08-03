package com.example.guru2_re

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log

class ScreenOffReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "MyApp::ScreenOffTag")

            wakeLock.acquire(3000) // Acquire the wake lock for 3 seconds
            wakeLock.release() // Release the wake lock to let the screen turn off

            Log.d("ScreenOffReceiver", "Screen dimmed and then turned off")
        } catch (e: Exception) {
            Log.e("ScreenOffReceiver", "Failed to turn off screen: ${e.message}")
        }
    }
}
