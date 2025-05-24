package com.example.profile.domain

import com.example.profile.data.Profile

interface ProfileRepository {
    suspend fun saveProfile(profile: Profile): Boolean
    suspend fun loadProfile(userId: String): Profile?
}