package com.digital.profile.data.repositoryimpl

import com.digital.profile.data.model.Profile
import com.digital.profile.domain.repository.ProfileRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient
) : ProfileRepository {
    override suspend fun getProfile(userId: String) = supabase
        .from("profiles")
        .select()
        //.eq("user_id", userId)
        //.single()
        .decodeAs<Profile>()
}