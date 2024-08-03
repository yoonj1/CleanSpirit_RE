package com.example.guru2_re

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var timerTitle: TextView
    private lateinit var timerText: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var btnDelete: ImageView
    private lateinit var btnPause: ImageView
    private lateinit var btnReload: ImageView
    private lateinit var btnPomodoro40: Button
    private lateinit var btnPomodoro50: Button
    private lateinit var btnMathExam: Button
    private lateinit var btnKoreanExam: Button
    private lateinit var settingsButton: AppCompatImageButton

    private var timerRunning = false
    private var startTimeMillis: Long = 0
    private var elapsedTimeMillis: Long = 0
    private var initialTimeMillis: Long = 0
    private val handler: Handler = Handler()

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            val elapsedMillis = SystemClock.elapsedRealtime() - startTimeMillis
            val totalMillis = initialTimeMillis - elapsedMillis
            if (totalMillis <= 0) {
                updateTimerText(0)
                progressBar.progress = 100
                stopTimer()
                return
            }
            updateTimerText(totalMillis)
            updateProgressBar(totalMillis)
            showNotification(totalMillis)
            handler.postDelayed(this, 1000) // 매초 호출
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI 요소 초기화
        timerTitle = findViewById(R.id.timerTitle)
        timerText = findViewById(R.id.timerText)
        progressBar = findViewById(R.id.progressBar)
        btnDelete = findViewById(R.id.btnDelete)
        btnPause = findViewById(R.id.btnPause)
        btnReload = findViewById(R.id.btnReload)
        btnPomodoro40 = findViewById(R.id.btnPomodoro40)
        btnPomodoro50 = findViewById(R.id.btnPomodoro50)
        btnMathExam = findViewById(R.id.btnMathExam)
        btnKoreanExam = findViewById(R.id.btnKoreanExam)
        settingsButton = findViewById(R.id.settingsButton)

        btnPomodoro40.setOnClickListener { setTimer(40) }
        btnPomodoro50.setOnClickListener { setTimer(50) }
        btnMathExam.setOnClickListener { setTimer(100) }
        btnKoreanExam.setOnClickListener { setTimer(80) }

        settingsButton.setOnClickListener {
            showPopupMenu(it)
        }

        btnDelete.setOnClickListener {
            resetTimer()
        }

        btnPause.setOnClickListener {
            if (timerRunning) {
                stopTimer()
                btnPause.setImageResource(R.drawable.play) // 변경된 이미지 리소스 사용
            } else {
                startTimer()
                btnPause.setImageResource(R.drawable.pause) // 변경된 이미지 리소스 사용
            }
        }

        btnReload.setOnClickListener {
            resetTime()
        }

        timerText.setOnClickListener {
            showTimePickerDialog()
        }

        // 권한 확인 및 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkNotificationPermission()
        }
    }

    private fun startTimer() {
        if (!timerRunning) {
            startTimeMillis = SystemClock.elapsedRealtime()
            handler.post(runnable)
            timerRunning = true
        }
    }

    private fun stopTimer() {
        if (timerRunning) {
            elapsedTimeMillis += SystemClock.elapsedRealtime() - startTimeMillis
            handler.removeCallbacks(runnable)
            timerRunning = false
            // 타이머 정지 시 진행 상태 업데이트 보장
            updateProgressBar(elapsedTimeMillis)
        }
    }

    private fun resetTimer() {
        handler.removeCallbacks(runnable)
        progressBar.progress = 0
        progressBar.progressDrawable = ContextCompat.getDrawable(this, R.drawable.circle_progress_bar)
        updateTimerText(0)
        timerRunning = false
    }

    private fun resetTime() {
        stopTimer()
        elapsedTimeMillis = initialTimeMillis // 초기 시간으로 재설정
        updateTimerText(elapsedTimeMillis)
        updateProgressBar(elapsedTimeMillis)
    }

    private fun setTimer(minutes: Int) {
        initialTimeMillis = minutes * 60 * 1000L
        elapsedTimeMillis = initialTimeMillis
        updateTimerText(elapsedTimeMillis)
        updateProgressBar(elapsedTimeMillis)
        // 타이머 설정 후 자동으로 시작
        if (timerRunning) {
            stopTimer() // 기존 타이머 중지
        }
        startTimer()
    }

    private fun updateTimerText(millis: Long) {
        val seconds = (millis / 1000).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        timerText.setText(String.format("%02d:%02d", minutes, remainingSeconds))
    }

    private fun updateProgressBar(millis: Long) {
        val progressPercentage = ((initialTimeMillis - millis) / initialTimeMillis.toFloat() * 100).toInt()
        Log.d("ProgressBar", "Updating progress: $progressPercentage%")
        progressBar.progress = progressPercentage.coerceIn(0, 100) // 진행 상태를 0과 100 사이로 보장
    }

    private fun showNotification(millis: Long) {
        val channelId = "timer_channel"
        val notificationId = 1

        // 알림 채널 설정 (안드로이드 8.0 이상)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 권한이 있는지 확인하고, 권한이 없는 경우 예외 처리
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                val channel = NotificationChannel(
                    channelId,
                    "Timer Notifications",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Notifications for timer updates"
                }
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            } else {
                // 권한이 없는 경우의 처리
                Log.e("Notification", "Notification permission not granted")
                return
            }
        }

        val notificationText = String.format("Time left: %02d:%02d", millis / 60000, (millis % 60000) / 1000)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification) // 알림 아이콘
            .setContentTitle("Timer Update")
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, notificationBuilder.build())
        }
    }

    private fun showTimePickerDialog() {
        // 시간 선택 대화 상자 구현
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_settings -> {
                    Log.d("MainActivity", "Settings selected")
                    // Settings activity 시작
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_full_lock -> {
                    Log.d("MainActivity", "Full Lock selected")
                    // Full lock activity 시작
                    val intent = Intent(this, FullLockActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun checkNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
