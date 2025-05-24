package com.example.cardss.domain

import com.example.cardss.data.CardsRepository
import com.example.cardss.data.LikeEntry
import com.example.profile.data.Profile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class CardsRepositoryImpl(
    private val supabaseClient: SupabaseClient
) : CardsRepository {

    override suspend fun getCurrentUserId(): String? {
        return supabaseClient.auth.currentUserOrNull()?.id
    }

    override suspend fun getAllLikes(): List<LikeEntry> {
        return supabaseClient
            .from("likes")
            .select()
            .decodeList<LikeEntry>() ?: emptyList()
    }

    override suspend fun getAllProfiles(): List<Profile> {
        return supabaseClient
            .from("profiles")
            .select()
            .decodeList<Profile>() ?: emptyList()
    }

    override suspend fun upsertLike(fromUserId: String, toUserId: String, status: String) {
        supabaseClient.from("likes").upsert(
            mapOf(
                "from_user_id" to fromUserId,
                "to_user_id" to toUserId,
                "status" to status
            )
        )
    }

    override suspend fun deleteLike(fromUserId: String, toUserId: String, status: String) {
        supabaseClient.from("likes").delete {
            filter {
                eq("from_user_id", fromUserId)
                eq("to_user_id", toUserId)
                eq("status", status)
            }
        }
    }
}