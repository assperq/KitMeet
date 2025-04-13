package com.digital.profile.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val user_id: String,
    val email: String,
    val full_name: String?,
    val avatar_url: String?,
    val bio: String?,
    val age: Int?,
    val profession: String?
)