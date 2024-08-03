package com.example.guru2_re

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonSignIn: Button
    private lateinit var buttonRegister: Button

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // View 초기화
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonSignIn = findViewById(R.id.buttonSignIn)
        buttonRegister = findViewById(R.id.buttonRegister)

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

        buttonSignIn.setOnClickListener {
            signIn()
        }

        buttonRegister.setOnClickListener {
            register()
        }
    }

    private fun signIn() {
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()

        // 이메일과 비밀번호가 비어있는지 확인
        if (email.isNotEmpty() && password.isNotEmpty()) {
            val registeredEmail = sharedPreferences.getString("registered_email", null)
            val registeredPassword = sharedPreferences.getString("registered_password", null)

            Log.d("LoginActivity", "Entered Email: $email")
            Log.d("LoginActivity", "Entered Password: $password")
            Log.d("LoginActivity", "Stored Email: $registeredEmail")
            Log.d("LoginActivity", "Stored Password: $registeredPassword")

            if (registeredEmail == email && registeredPassword == password) {
                // 로그인 성공
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // 로그인 실패
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Email and password must not be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun register() {
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()

        // 이메일과 비밀번호가 비어있는지 확인
        if (email.isNotEmpty() && password.isNotEmpty()) {
            val registeredEmail = sharedPreferences.getString("registered_email", null)

            if (registeredEmail == null) {
                // 첫 번째 등록
                saveUser(email, password)
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                // 이미 등록된 사용자
                Toast.makeText(this, "User already registered", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Email and password must not be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUser(email: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString("registered_email", email)
        editor.putString("registered_password", password)
        editor.apply()

        Log.d("LoginActivity", "Saved Email: $email")
        Log.d("LoginActivity", "Saved Password: $password")
    }
}
