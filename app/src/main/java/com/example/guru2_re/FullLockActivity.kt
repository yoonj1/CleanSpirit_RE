package com.example.guru2_re

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FullLockActivity : AppCompatActivity() {

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var compName: ComponentName
    private val RESULT_ENABLE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)  // 확인 필요

        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        compName = ComponentName(this, MyDeviceAdminReceiver::class.java)

        val timeInput: EditText = findViewById(R.id.timeInput)  // 확인 필요
        val startButton: Button = findViewById(R.id.startButton)  // 확인 필요
        val lockToggle: Switch = findViewById(R.id.lockToggle)  // 확인 필요

        startButton.setOnClickListener {
            val time = timeInput.text.toString()
            val timeParts = time.split(":")
            if (timeParts.size == 2) {
                val minutes = timeParts[0].toIntOrNull()
                val seconds = timeParts[1].toIntOrNull()
                if (minutes != null && seconds != null) {
                    val totalMillis = (minutes * 60 + seconds) * 1000L
                    startTimer(totalMillis, lockToggle.isChecked)
                } else {
                    Toast.makeText(this, "시간을 올바르게 입력하세요.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "시간을 올바르게 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        lockToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && !devicePolicyManager.isAdminActive(compName)) {
                enableDeviceAdmin()
                lockToggle.isChecked = false // 권한을 얻기 전까지는 토글을 비활성화 상태로 유지
            }
        }
    }

    private fun startTimer(millis: Long, enableLock: Boolean) {
        val handler = Handler(mainLooper)
        handler.postDelayed({
            if (enableLock) {
                if (devicePolicyManager.isAdminActive(compName)) {
                    try {
                        devicePolicyManager.lockNow()
                    } catch (e: SecurityException) {
                        Toast.makeText(this, "Device Admin 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                        enableDeviceAdmin()
                    }
                } else {
                    Toast.makeText(this, "Device Admin 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                    enableDeviceAdmin()
                }
            }
        }, millis)
    }

    private fun enableDeviceAdmin() {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "권한을 활성화하면 이 기능을 사용할 수 있습니다.")
        startActivityForResult(intent, RESULT_ENABLE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_ENABLE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Device Admin 권한이 활성화되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Device Admin 권한을 활성화하지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
