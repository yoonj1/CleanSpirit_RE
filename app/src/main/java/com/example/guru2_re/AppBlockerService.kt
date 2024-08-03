package com.example.guru2_re

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder

class AppBlockerService : Service() {

    private val binder = TimerBinder()
    private var timer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0
    private var initialTimeInMillis: Long = 0

    inner class TimerBinder : Binder() {
        fun getService(): AppBlockerService = this@AppBlockerService
    }

    override fun onCreate() {
        super.onCreate()
        // 포그라운드 서비스 관련 코드 삭제
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 포그라운드 서비스 관련 코드 삭제
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun startTimer(timeInMillis: Long) {
        initialTimeInMillis = timeInMillis
        timeLeftInMillis = timeInMillis
        timer?.cancel()
        timer = object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                val intent = Intent("TIMER_UPDATED")
                intent.putExtra("timeLeft", timeLeftInMillis)
                sendBroadcast(intent)
                // 알림 업데이트 코드 삭제
            }

            override fun onFinish() {
                timeLeftInMillis = 0
                val intent = Intent("TIMER_UPDATED")
                intent.putExtra("timeLeft", timeLeftInMillis)
                sendBroadcast(intent)
                stopSelf()
            }
        }.start()
    }

    fun stopTimer() {
        timer?.cancel()
        timeLeftInMillis = 0
        val intent = Intent("TIMER_UPDATED")
        intent.putExtra("timeLeft", timeLeftInMillis)
        sendBroadcast(intent)
        stopSelf()
    }

    fun pauseTimer() {
        timer?.cancel()
    }

    fun resumeTimer() {
        startTimer(timeLeftInMillis)
    }

    fun resetTimer() {
        startTimer(initialTimeInMillis)
    }

    fun getTimeLeft(): Long {
        return timeLeftInMillis
    }

    fun getInitialTimeInMillis(): Long {
        return initialTimeInMillis
    }
}
