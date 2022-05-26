package com.example.refuapp.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.refuapp.databinding.ActivitySigninBinding

class SigninActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
    }
}