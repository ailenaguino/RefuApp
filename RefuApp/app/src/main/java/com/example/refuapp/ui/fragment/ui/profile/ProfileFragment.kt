package com.example.refuapp.ui.fragment.ui.profile

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.refuapp.databinding.ProfileFragmentBinding
import com.example.refuapp.ui.view.CreatePostActivity
import com.example.refuapp.ui.view.EditProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {


    private lateinit var auth: FirebaseAuth
    private val database = Firebase.firestore

    private var _binding: ProfileFragmentBinding? = null

    private val binding get() = _binding!!

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = Firebase.auth

        getUserData()

        _binding = ProfileFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setListeners()
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun setListeners() {
        binding.icProfileEdit.setOnClickListener{
            goToEditProfile()
        }
        binding.fabCreatePost.setOnClickListener{
            goToCreatePost()
        }
    }

    private fun goToCreatePost() {
        val intent = Intent(activity, CreatePostActivity::class.java)
        startActivity(intent)
    }

    private fun goToEditProfile() {
        val intent = Intent(activity, EditProfileActivity::class.java)
        startActivity(intent)
    }

    private fun getUserData(){
        val user = Firebase.auth.currentUser
        var email = ""
        user?.let {
            email = user.email.toString()
        }
        database.collection("users").document(email)
        .get().addOnSuccessListener { documentSnapshot ->
            val username = documentSnapshot.data?.get("username")
            val name = documentSnapshot.data?.get("name")
            val biography = documentSnapshot.data?.get("biography")
            val profilePicture = documentSnapshot.data?.get("profilePicture")

                binding.txtProfileName.text = name.toString()
                binding.txtProfileBiography.text = biography.toString()
                binding.txtProfileUsername.text = "@"+username.toString()
                Picasso.get().load(profilePicture.toString()).into(binding.ivProfilePicture)
        }.addOnFailureListener{
                Log.i("Firestore", "Falló la obtencion del usuario")
            }
    }

}