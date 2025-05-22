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
    var looking_for: String,
    var about_me: String,
    val gender: String,
    var age: Int,
    val status: String,
    var specialty: String
) {
    constructor() : this(
        user_id = "",
        name = "",
        profession = "",
        group = "",
        main_photo = "",
        gallery_photos = emptyList(),
        looking_for = "",
        about_me = "",
        gender = "",
        age = -1,
        status = "",
        specialty = ""
    )
}