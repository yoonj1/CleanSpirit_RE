package com.example.guru2_re

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var switchService: Switch
    private lateinit var listView: ListView

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // 뷰 초기화
        btnBack = findViewById(R.id.btnBack)
        switchService = findViewById(R.id.switchService)
        listView = findViewById(R.id.listView)

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

        // 저장된 설정 불러오기
        loadSettings()

        // 뒤로 가기 버튼 클릭 리스너 설정
        btnBack.setOnClickListener {
            finish() // 현재 액티비티를 종료하고 이전 화면으로 돌아갑니다
        }

        // 스위치 상태 변경 리스너 설정
        switchService.setOnCheckedChangeListener { _, isChecked ->
            // 스위치 상태 변경 처리
            saveSettings()
        }

        // ListView 설정
        setupListView()
    }

    private fun loadSettings() {
        // SharedPreferences에서 저장된 설정 불러오기
        switchService.isChecked = sharedPreferences.getBoolean("app_blocker_enabled", false)
    }

    private fun saveSettings() {
        // SharedPreferences에 설정 저장
        val editor = sharedPreferences.edit()
        editor.putBoolean("app_blocker_enabled", switchService.isChecked)
        editor.apply() // 변경 사항을 비동기적으로 적용
    }

    private fun loadInstalledApps() {
        val packageManager: PackageManager = packageManager
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val appNames = packages.map { packageManager.getApplicationLabel(it).toString() }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, appNames)
        listView.adapter = adapter
    }

    private fun setupListView() {
        // 설치된 앱 목록 로드
        loadInstalledApps()
    }
}
