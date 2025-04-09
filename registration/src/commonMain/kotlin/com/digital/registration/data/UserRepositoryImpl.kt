package com.digital.registration.data

import com.digital.registration.domain.UserRepository

class UserRepositoryImpl(
    private val datasource: UserRemoteDatasource
) : UserRepository {
    override suspend fun singIn(
        email: String,
        password: String
    ): Result<Unit> {
        return datasource.singIn(email, password)
    }

    override suspend fun singUp(
        email: String,
        password: String
    ): Result<Unit> {
        return datasource.singUp(email, password)
    }

}