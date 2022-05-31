package com.example.refuapp.domain.model

data class User(val username: String,
                val name: String,
                val biography: String,
                val email: String,
                val isShelter: Boolean,
                val profilePicture: String)