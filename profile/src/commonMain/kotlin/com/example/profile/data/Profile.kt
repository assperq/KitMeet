package com.example.profile.data

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val user_id: String,
    val name: String,
    val profession: String,
    val group: String
)