package com.digital.profile.domain.model

data class Profile(
    val id: Int,
    val name: String,
    val age: Int,
    val group: String,
    val position: String,
    val lookingFor: String,
    val aboutMe: String,
    val photos: List<String>,
    val mainPhoto: String
)