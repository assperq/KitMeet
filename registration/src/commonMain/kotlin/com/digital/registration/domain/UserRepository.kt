package com.digital.registration.domain

interface UserRepository {
    suspend fun singIn(email: String, password: String) : Result<Unit>
    suspend fun singUp(email: String, password: String) : Result<Unit>
}