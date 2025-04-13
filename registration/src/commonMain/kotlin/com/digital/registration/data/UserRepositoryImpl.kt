package com.digital.registration.data

import com.digital.registration.domain.UserRepository
import com.example.profile.data.UserProfile

class UserRepositoryImpl(
    private val datasource: UserRemoteDatasource
) : UserRepository {
    override suspend fun singIn(email: String, password: String): Result<Unit> {
        return try {
            datasource.singIn(email, password)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun singUp(email: String, password: String): Result<Unit> {
        return try {
            datasource.singUp(email, password)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getProfile(userId: String): Result<UserProfile> {
        return try {
            datasource.getProfile(userId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun saveProfile(profile: UserProfile): Result<Unit> {
        return try {
            datasource.saveProfile(profile)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun uploadAvatar(userId: String, imageData: ByteArray): Result<String> {
        return try {
            datasource.uploadAvatar(userId, imageData)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}