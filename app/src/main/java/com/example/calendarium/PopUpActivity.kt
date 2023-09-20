package com.example.calendarium

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.calendarium.databinding.ActivityPopUpBinding

class PopUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPopUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPopUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }


}