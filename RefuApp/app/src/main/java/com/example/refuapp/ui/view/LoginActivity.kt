package com.example.refuapp.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.refuapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        auth = Firebase.auth
        setListeners()
    }

    private fun setListeners(){
        binding.btnGoToSignin.setOnClickListener {
            goToSignin()
        }
    }

    private fun goToSignin(){
        val intent = Intent(this, SigninActivity::class.java)
        startActivity(intent)
    }
}