package com.example.profile.data

import com.example.profile.domain.ProfileRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class ProfileRepositoryImpl(
    private val supabaseClient: SupabaseClient
) : ProfileRepository {

    override suspend fun saveProfile(profile: Profile): Boolean {
        return try {
            val response = supabaseClient
                .from("profiles")
                .upsert(profile)

            println("📦 Supabase response: $response")
            true
        } catch (e: Exception) {
            println("❌ Ошибка при сохранении профиля: ${e.message}")
            false
        }
    }

    override suspend fun loadProfile(userId: String): Profile? {
        return try {
            val profile = supabaseClient
                .from("profiles")
                .select {
                    filter { eq("user_id", userId) }
                    limit(1)
                }
                .decodeSingle<Profile>()

            println("📥 Профиль загружен: $profile")
            profile
        } catch (e: Exception) {
            println("⚠️ Не удалось загрузить профиль: ${e.message}")
            null
        }
    }
}