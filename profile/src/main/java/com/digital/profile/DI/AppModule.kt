package com.digital.profile.DI

import com.digital.profile.data.repository.ProfileRepositoryImpl
import com.digital.profile.data.source.FakeProfileDataSource
import com.digital.profile.domain.repository.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideProfileRepository(): ProfileRepository {
        return ProfileRepositoryImpl(FakeProfileDataSource())
    }
}