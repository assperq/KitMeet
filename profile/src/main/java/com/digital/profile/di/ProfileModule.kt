package com.digital.profile.di

import com.digital.profile.data.repositoryimpl.ProfileRepositoryImpl
import com.digital.profile.domain.repository.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {
    @Provides
    fun provideProfileRepository(supabase: SupabaseClient): ProfileRepository {
        return ProfileRepositoryImpl(supabase)
    }
}