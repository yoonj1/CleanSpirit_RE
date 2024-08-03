package com.example.guru2_re

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class PartialLockActivity : AppCompatActivity() {

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var compName: ComponentName
    private lateinit var deviceAdminLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blocked_app_layout)

        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        compName = ComponentName(this, MyDeviceAdminReceiver::class.java)

        deviceAdminLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                Toast.makeText(this, "Device Admin 권한이 활성화되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Device Admin 권한을 활성화하지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startTimer(millis: Long, enableLock: Boolean) {
        val handler = Handler(Looper.getMainLooper())
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
        deviceAdminLauncher.launch(intent)
    }
}
