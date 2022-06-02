package com.example.refuapp.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.example.refuapp.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class EditProfileActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private val database = Firebase.firestore
    private lateinit var binding: ActivityEditProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        auth = Firebase.auth
        getUserData()
    }

    private fun getUserData() {
        val user = Firebase.auth.currentUser
        var email = ""
        user?.let {
            email = user.email.toString()
        }
        database.collection("users").document(email)
            .get().addOnSuccessListener { documentSnapshot ->
                val name = documentSnapshot.data?.get("name")
                val biography = documentSnapshot.data?.get("biography")
                val profilePicture = documentSnapshot.data?.get("profilePicture")

                binding.txtEditNameInput.hint = name.toString()
                binding.txtEditBiographyInput.hint = biography.toString()
                Picasso.get().load(profilePicture.toString()).into(binding.ivEditProfilePicture)
            }.addOnFailureListener{
                Log.i("Firestore", "Falló la obtencion de los datos del usuario")
            }
    }
}