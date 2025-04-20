package com.example.profile.data

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val user_id: String,
    val name: String,
    val profession: String,
    val group: String,
    val main_photo: String? = null,
    val gallery_photos: List<String>? = null,
    val looking_for: String,
    val about_me: String
)