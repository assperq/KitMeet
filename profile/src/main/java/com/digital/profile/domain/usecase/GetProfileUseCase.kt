package com.digital.profile.domain.usecase

import com.digital.profile.domain.model.Profile
import com.digital.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(userId: Int): Profile {
        return repository.getProfile(userId)
    }

    fun observeProfile(userId: Int): Flow<Profile> {
        return repository.observeProfileUpdates(userId)
    }
}