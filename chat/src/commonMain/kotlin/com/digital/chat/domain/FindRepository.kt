package com.digital.chat.domain

import com.example.profile.data.Profile

interface FindRepository {
    suspend fun findUsers(query : String, currentUser : String) : List<Profile>
}