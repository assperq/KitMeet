package com.digital.chat.data

import com.digital.chat.domain.FindRepository
import com.digital.supabaseclients.SupabaseManager
import com.example.profile.data.Profile
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.TextSearchType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement

class FindRepositoryImpl : FindRepository {
    override suspend fun findUsers(query: String, currentUser : String): List<Profile> {
        return SupabaseManager.supabaseClient.postgrest.rpc(
            "search_available_profiles",
            parameters = buildJsonObject {
                put("p_current_user", Json.encodeToJsonElement(currentUser))
                put("p_query", Json.encodeToJsonElement(query))
            }).decodeList()
    }
}