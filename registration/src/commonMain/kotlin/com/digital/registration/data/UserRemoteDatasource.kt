package com.digital.registration.data

import com.example.profile.data.UserProfile

interface UserRemoteDatasource {
    suspend fun singIn(email: String, password: String): Result<Unit>
    suspend fun singUp(email: String, password: String): Result<Unit>
    suspend fun getProfile(userId: String): Result<UserProfile>
    suspend fun saveProfile(profile: UserProfile): Result<Unit>
}