package com.digital.registration.data

import com.digital.supabaseclients.SupabaseManager.supabaseClient
import com.example.profile.data.UserProfile
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.postgrest


class UserRemoteDatasourceImpl : UserRemoteDatasource {
    override suspend fun singIn(email: String, password: String): Result<Unit> {
        return try {
            supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun singUp(email: String, password: String): Result<Unit> {
        return try {
            supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProfile(userId: String): Result<UserProfile> {
        return try {
            val profile = supabaseClient.postgrest["profiles"]
                .select(columns = Columns.list("user_id", "name", "age", "avatar_url")) {
                    eq("user_id", userId)
                }
                .decodeSingle<UserProfile>()
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveProfile(profile: UserProfile): Result<Unit> {
        return try {
            supabaseClient.postgrest["profiles"]
                .upsert(profile)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadAvatar(userId: String, imageData: ByteArray): Result<String> {
        return try {
            val path = "avatars/$userId/${System.currentTimeMillis()}.jpg"
            supabaseClient.storage
                .from("avatars")
                .upload(path, imageData, upsert = true)
            Result.success(path)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
