package com.digital.registration.domain

import com.example.profile.data.UserProfile

interface UserRepository {
    suspend fun singIn(email: String, password: String): Result<Unit>
    suspend fun singUp(email: String, password: String): Result<Unit>
    suspend fun getProfile(userId: String): Result<UserProfile>
    suspend fun saveProfile(profile: UserProfile): Result<Unit>
    suspend fun uploadAvatar(userId: String, imageData: ByteArray): Result<String>
}