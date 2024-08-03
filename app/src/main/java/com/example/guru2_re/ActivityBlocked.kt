package com.example.guru2_re

import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class ActivityBlocked : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blocked_app_layout)

        val appNameTextView = findViewById<TextView>(R.id.appNameTextView)
        val appIconImageView = findViewById<ImageView>(R.id.appIconImageView)
        val timerCircleView = findViewById<ProgressBar>(R.id.circularTimerView)
        val timeLeftTextView = findViewById<TextView>(R.id.timeLeftTextView)

        // Get the blocked app's package name from the intent
        val packageName = intent.getStringExtra("PACKAGE_NAME")

        if (packageName != null) {
            val packageManager = packageManager
            val appInfo = packageManager.getLaunchIntentForPackage(packageName)
            if (appInfo != null) {
                val resolveInfo: ResolveInfo? = packageManager.resolveActivity(appInfo, PackageManager.MATCH_DEFAULT_ONLY)
                if (resolveInfo != null) {
                    appNameTextView.text = resolveInfo.loadLabel(packageManager).toString()
                    appIconImageView.setImageDrawable(resolveInfo.loadIcon(packageManager))
                }
            }
        }

        // Configure the ProgressBar
        timerCircleView.max = 100 // 최대값 설정
        timerCircleView.progress = 1 // 초기 진행 상태
        timerCircleView.progressDrawable = ContextCompat.getDrawable(this, R.drawable.circle_progress_bar)

        // Update timer and progress bar as needed
    }
}
