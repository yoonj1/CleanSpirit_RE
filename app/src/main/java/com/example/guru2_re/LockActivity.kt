package com.example.guru2_re

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class LockActivity : AppCompatActivity() {

    private lateinit var timeInput: EditText
    private lateinit var lockToggle: Switch
    private lateinit var startButton: Button
    private var lockTimeInMillis: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)

        // Initialize UI elements
        timeInput = findViewById(R.id.timeInput)
        lockToggle = findViewById(R.id.lockToggle)
        startButton = findViewById(R.id.startButton)

        // Set listeners
        timeInput.setOnClickListener { showTimePickerDialog() }
        lockToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (lockTimeInMillis > 0) {
                    setAlarm()
                } else {
                    Toast.makeText(this, "Invalid time input", Toast.LENGTH_SHORT).show()
                }
            } else {
                cancelAlarm()
            }
        }
        startButton.setOnClickListener { startLockTimer() }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
            val time = String.format("%02d:%02d", selectedHour, selectedMinute)
            timeInput.setText(time)
            lockTimeInMillis = (selectedHour * 3600 + selectedMinute * 60) * 1000L
        }, hour, minute, true)

        timePickerDialog.show()
    }

    private fun setAlarm() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MILLISECOND, lockTimeInMillis.toInt())

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ScreenOffReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Toast.makeText(this, "Alarm set for ${lockTimeInMillis / 1000} seconds from now", Toast.LENGTH_SHORT).show()
    }

    private fun cancelAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ScreenOffReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.cancel(pendingIntent)
        Toast.makeText(this, "Alarm canceled", Toast.LENGTH_SHORT).show()
    }

    private fun startLockTimer() {
        if (lockTimeInMillis > 0) {
            if (lockToggle.isChecked) {
                setAlarm()
            } else {
                Toast.makeText(this, "Enable the lock toggle", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Invalid time input", Toast.LENGTH_SHORT).show()
        }
    }
}
