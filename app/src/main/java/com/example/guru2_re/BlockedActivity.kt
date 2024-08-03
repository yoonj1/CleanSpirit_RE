package com.example.guru2_re

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BlockedActivity : AppCompatActivity() {

    private lateinit var blockedMessageTextView: TextView
    private lateinit var appNameTextView: TextView
    private lateinit var appIconImageView: ImageView
    private lateinit var timerCircleContainer: RelativeLayout
    private lateinit var timeLeftTextView: TextView
    private lateinit var listView: ListView
    private lateinit var circularTimerView: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blocked_app_layout)

        // View initialization
        blockedMessageTextView = findViewById(R.id.blockedMessageTextView)
        appNameTextView = findViewById(R.id.appNameTextView)
        appIconImageView = findViewById(R.id.appIconImageView)
        timerCircleContainer = findViewById(R.id.timerCircleContainer)
        timeLeftTextView = findViewById(R.id.timeLeftTextView)
        listView = findViewById(R.id.listView)
        circularTimerView = findViewById(R.id.circularTimerView)

        // Sample data, replace with actual app data
        val appPackageName = intent.getStringExtra("APP_PACKAGE_NAME") ?: "com.example.someapp"
        displayAppInfo(appPackageName)

        // Timer logic
        startTimer(1) // Example: Start a 1-minute timer
    }

    private fun displayAppInfo(packageName: String) {
        try {
            val packageManager = packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            val appName = packageManager.getApplicationLabel(applicationInfo).toString()
            val appIcon = packageManager.getApplicationIcon(applicationInfo)

            appNameTextView.text = appName
            appIconImageView.setImageDrawable(appIcon)

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            appNameTextView.text = "Unknown App"
            appIconImageView.setImageDrawable(null)
        }
    }

    private fun startTimer(minutes: Int) {
        val totalTimeInMillis = (minutes * 60 * 1000).toLong()
        val countDownTimer = object : CountDownTimer(totalTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutesLeft = (millisUntilFinished / (60 * 1000)).toInt()
                val secondsLeft = (millisUntilFinished % (60 * 1000) / 1000).toInt()
                timeLeftTextView.text = String.format("%02d:%02d", minutesLeft, secondsLeft)

                val progress = ((totalTimeInMillis - millisUntilFinished) * 100 / totalTimeInMillis).toInt()
                circularTimerView.progress = progress
            }

            override fun onFinish() {
                timeLeftTextView.text = "00:00"
                // Handle timer finish event
            }
        }
        countDownTimer.start()
    }
}
