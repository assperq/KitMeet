package com.example.cardss.data

import kotlinx.serialization.Serializable

@Serializable
data class LikeEntry(
    val from_user_id: String,
    val to_user_id: String,
    val status: String
)
