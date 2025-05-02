package com.example.profile

import android.content.Context
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import com.digital.supabaseclients.SupabaseManager
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage

// В androidMain
suspend fun uploadImageToSupabase(
    context: Context,
    userId: String,
    uri: Uri,
    fileName: String
): String? {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return null
    val byteArray: ByteArray = inputStream.readBytes()
    val path = "profile-photos/$userId/$fileName"

    SupabaseManager.supabaseClient.storage
        .from("profile-photos")
        .upload(path = path, data = byteArray, options = { upsert = true })

    return SupabaseManager.supabaseClient.storage
        .from("profile-photos")
        .publicUrl(path)
}