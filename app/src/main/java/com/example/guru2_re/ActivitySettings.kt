package com.example.guru2_re

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ActivitySettings : AppCompatActivity() {

    private lateinit var appBlockerSwitch: Switch
    private lateinit var listView: ListView
    private lateinit var adapter: AppListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // UI 요소 초기화
        appBlockerSwitch = findViewById(R.id.switchService)
        listView = findViewById(R.id.listView)
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            Log.d("ActivitySettings", "뒤로가기 버튼 클릭됨")
            finish()
        }

        // 스위치에 대한 리스너 설정
        appBlockerSwitch.setOnCheckedChangeListener { _, isChecked ->
            Log.d("ActivitySettings", "스위치 토글됨: $isChecked")
            if (isChecked) {
                requestPermissionAndSetupAppBlocker()
            } else {
                disableAppBlocker()
            }
        }
        1
    }

    private fun requestPermissionAndSetupAppBlocker() {
        if (isUsageStatsPermissionGranted()) {
            enableAppBlocker()
        } else {
            Log.d("ActivitySettings", "사용 통계 권한 요청 중")
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivityForResult(intent, Companion.REQUEST_CODE_USAGE_STATS)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Companion.REQUEST_CODE_USAGE_STATS) {
            if (isUsageStatsPermissionGranted()) {
                enableAppBlocker()
            } else {
                Toast.makeText(this, "권한이 허용되지 않았습니다. 앱 차단기를 활성화할 수 없습니다.", Toast.LENGTH_SHORT).show()
                appBlockerSwitch.isChecked = false
            }
        }
    }

    private fun disableAppBlocker() {
        // 앱 차단기 비활성화 로직
        Log.d("ActivitySettings", "앱 차단기 비활성화됨")
        Toast.makeText(this, "앱 차단기 비활성화됨", Toast.LENGTH_SHORT).show()
    }

    private fun enableAppBlocker() {
        // 앱 차단기 활성화 로직
        Log.d("ActivitySettings", "앱 차단기 활성화됨")
        Toast.makeText(this, "앱 차단기 활성화됨", Toast.LENGTH_SHORT).show()
    }


    private fun isUsageStatsPermissionGranted(): Boolean {
        return try {
            val appOpsManager = getSystemService(APP_OPS_SERVICE) as android.app.AppOpsManager
            val packageName = packageName
            val mode = appOpsManager.checkOpNoThrow(
                android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                packageName
            )
            mode == android.app.AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            Log.e("ActivitySettings", "사용 통계 권한 확인 중 오류 발생", e)
            false
        }
    }

    companion object {
        private const val REQUEST_CODE_USAGE_STATS = 100
    }
}
