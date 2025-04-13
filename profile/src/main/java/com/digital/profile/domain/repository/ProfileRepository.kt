package com.digital.profile.domain.repository

import com.digital.profile.data.model.Profile

interface ProfileRepository {
    suspend fun getProfile(userId: String): Profile
}