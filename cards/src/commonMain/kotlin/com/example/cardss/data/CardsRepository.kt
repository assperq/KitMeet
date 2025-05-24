package com.example.cardss.data

import com.example.profile.data.Profile

interface CardsRepository {
    suspend fun getCurrentUserId(): String?
    suspend fun getAllLikes(): List<LikeEntry>
    suspend fun getAllProfiles(): List<Profile>
    suspend fun upsertLike(fromUserId: String, toUserId: String, status: String)
    suspend fun deleteLike(fromUserId: String, toUserId: String, status: String)
}