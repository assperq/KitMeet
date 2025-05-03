package com.example.profile.data

interface ProfileRepository {
    fun getProfile(): Profile?
    fun saveProfile(profile: Profile)
}

class ProfileRepositoryImpl : ProfileRepository {
    private var currentProfile: Profile? = null

    override fun getProfile(): Profile? = currentProfile

    override fun saveProfile(profile: Profile) {
        currentProfile = profile
    }
}