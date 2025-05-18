package com.example.profile.data

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val user_id: String,
    var name: String,
    var profession: String,
    var group: String,
    val main_photo: String,
    val gallery_photos: List<String>,
    val looking_for: String,
    val about_me: String,
    val gender: String,
    val age: Int,
    val status: String,
    val specialty: String
)