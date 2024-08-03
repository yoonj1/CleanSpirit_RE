package com.example.guru2_re

import android.app.TimePickerDialog
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2_re.MyDeviceAdminReceiver
import java.util.Calendar

class ActivityLock : AppCompatActivity() {

    private lateinit var timeInput: EditText
    private lateinit var lockToggle: Switch
    private lateinit var lockButton: Button
    private var lockTimeInMillis: Long = 0
    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var adminComponentName: ComponentName
    private lateinit var handler: Handler
    private var startTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)

        // Initialize UI elements
        timeInput = findViewById(R.id.timeInput)
        lockToggle = findViewById(R.id.lockToggle)
        lockButton = findViewById(R.id.startButton)

        // Initialize DevicePolicyManager and ComponentName
        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponentName = ComponentName(this, MyDeviceAdminReceiver::class.java)
        handler = Handler(Looper.getMainLooper())

        // Set listeners
        timeInput.setOnClickListener { showTimePickerDialog() }
        lockToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (isDeviceAdminActive()) {
                    startLockTimer()
                } else {
                    activateDeviceAdmin()
                }
            } else {
                disableLockTimer()
            }
        }
        lockButton.setOnClickListener { lockDevice() }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this,
            { _, selectedHour, selectedMinute ->
                val time = String.format("%02d:%02d", selectedHour, selectedMinute)
                timeInput.setText(time)
                lockTimeInMillis = (selectedHour * 3600 + selectedMinute * 60) * 1000L
            }, hour, minute, true)

        timePickerDialog.show()
    }

    private fun isDeviceAdminActive(): Boolean {
        return devicePolicyManager.isAdminActive(adminComponentName)
    }

    private fun activateDeviceAdmin() {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponentName)
            putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Activate this app to use the screen lock feature.")
        }
        startActivityForResult(intent, REQUEST_CODE_DEVICE_ADMIN)
    }

    private fun startLockTimer() {
        if (lockTimeInMillis > 0) {
            if (isDeviceAdminActive()) {
                startTime = System.currentTimeMillis()
                updateTimer()
            } else {
                Toast.makeText(this, "Device admin not activated", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Invalid time input", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTimer() {
        val elapsed = System.currentTimeMillis() - startTime
        val remainingTime = lockTimeInMillis - elapsed

        if (remainingTime <= 0) {
            try {
                devicePolicyManager.lockNow()
            } catch (e: SecurityException) {
                Toast.makeText(this, "Failed to lock device: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            disableLockTimer()  // Ensure timer is disabled after lock
        } else {
            val minutes = (remainingTime / (1000 * 60) % 60).toInt()
            val seconds = (remainingTime / 1000 % 60).toInt()
            timeInput.setText(String.format("%02d:%02d", minutes, seconds))
            handler.postDelayed({ updateTimer() }, 1000)
        }
    }

    private fun disableLockTimer() {
        handler.removeCallbacksAndMessages(null)
        timeInput.setText("00:00")
        Toast.makeText(this, "Lock timer disabled", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_DEVICE_ADMIN) {
            if (isDeviceAdminActive()) {
                startLockTimer()
            } else {
                lockToggle.isChecked = false
                Toast.makeText(this, "Device admin not activated", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun lockDevice() {
        if (isDeviceAdminActive()) {
            try {
                devicePolicyManager.lockNow()
            } catch (e: SecurityException) {
                Toast.makeText(this, "Failed to lock device: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            activateDeviceAdmin()
        }
    }

    companion object {
        private const val REQUEST_CODE_DEVICE_ADMIN = 1
    }
}
