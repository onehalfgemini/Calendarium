package com.example.calendarium

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.calendarium.databinding.ActivityMainBinding
import com.example.calendarium.databinding.ActivitySignUpBinding
import android.widget.CalendarView //*
import android.widget.CalendarView.OnDateChangeListener
import android.widget.TextView


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val loginIntent= Intent(this, LogInActivity::class.java)
            startActivity(loginIntent)
        }

        binding.btnSignUp.setOnClickListener{
            val signupIntent = Intent(this, SignUpActivity::class.java)
            startActivity(signupIntent)
        }
    }
}