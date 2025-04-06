package com.digital.profile.domain.repository

import com.digital.profile.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getProfile(userId: Int): Profile
    fun observeProfileUpdates(userId: Int): Flow<Profile>
}