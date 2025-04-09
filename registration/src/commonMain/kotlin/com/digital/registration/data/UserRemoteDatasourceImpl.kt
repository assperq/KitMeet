package com.digital.registration.data

import com.digital.supabaseclients.SupabaseManager
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class UserRemoteDatasourceImpl : UserRemoteDatasource {
    override suspend fun singIn(email: String, password: String) : Result<Unit> {
        return try {
            SupabaseManager.supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun singUp(email: String, password: String) : Result<Unit> {
        return try {
            SupabaseManager.supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}