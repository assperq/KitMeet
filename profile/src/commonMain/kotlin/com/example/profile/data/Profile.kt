package com.example.profile.data

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val user_id: String,
    var name: String,
    var profession: String,
    var group: String,
    var main_photo: String,
    val gallery_photos: List<String>,
    var looking_for: String,
    var about_me: String,
    val gender: String,
    var age: Int,
    val status: String,
    var specialty: String,

    var friends: List<String> = emptyList(),
    var acceptedProfiles: List<String> = emptyList(),
    var rejectedProfiles: List<String> = emptyList()
)