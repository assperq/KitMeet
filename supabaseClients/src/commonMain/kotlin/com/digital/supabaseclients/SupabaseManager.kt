package com.digital.supabaseclients

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import io.ktor.http.auth.AuthScheme.Bearer
import io.ktor.http.headers

object SupabaseManager {
    val supabaseClient = createSupabaseClient(
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImttZWh4Z2RsbGpidHJmbmx6YmdyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDQwMTE4OTYsImV4cCI6MjA1OTU4Nzg5Nn0.x3yo7pd-763y4VMMyqr08Ef29eDSv_KA4N8IthKYtQk",
        supabaseUrl = "https://kmehxgdlljbtrfnlzbgr.supabase.co"
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
        install(Realtime)
        install(Functions)
    }
}