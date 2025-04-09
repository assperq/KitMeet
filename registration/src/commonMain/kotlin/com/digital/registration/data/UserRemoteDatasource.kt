package com.digital.registration.data

interface UserRemoteDatasource {
    suspend fun singIn(email: String, password: String) : Result<Unit>
    suspend fun singUp(email: String, password: String) : Result<Unit>
}