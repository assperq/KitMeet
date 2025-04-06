package com.digital.profile.data.repository

import com.digital.profile.data.source.FakeProfileDataSource
import com.digital.profile.domain.model.Profile
import com.digital.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val dataSource: FakeProfileDataSource
) : ProfileRepository {
    override suspend fun getProfile(userId: Int): Profile {
        return dataSource.getMockProfile()
    }

    override fun observeProfileUpdates(userId: Int): Flow<Profile> {
        return flow { emit(dataSource.getMockProfile()) }
    }
}