package org.digital.kitmeet.di

import com.digital.chat.data.ChatRepositoryImpl
import com.digital.chat.data.FindRepositoryImpl
import com.digital.chat.domain.ChatRepository
import com.digital.chat.domain.FindRepository
import com.digital.chat.presentation.ChatViewModel
import com.digital.chat.presentation.ui.find.FindViewModel
import com.digital.registration.data.UserRemoteDatasource
import com.digital.registration.data.UserRemoteDatasourceImpl
import com.digital.registration.data.UserRepositoryImpl
import com.digital.registration.domain.UserRepository
import com.digital.registration.presentation.RegistrationViewModel
import com.digital.settings.data.SettingsRepositoryImpl
import com.digital.settings.domain.SettingsRepository
import com.digital.settings.presentation.SettingsViewModel
import com.digital.supabaseclients.SupabaseManager
import com.example.cardss.CardsViewModel
import com.example.cardss.data.CardsRepository
import com.example.cardss.domain.CardsRepositoryImpl
import com.example.cardss.presentation.SwipeTracker
import com.example.profile.data.ProfileRepositoryImpl
import com.example.profile.domain.ProfileRepository
import com.example.profile.presentation.ProfileViewModel
import com.russhwolf.settings.Settings
import io.github.jan.supabase.SupabaseClient
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initKoin() = startKoin {
    modules(supabaseModule, settingsModule, cardsModule, chatModule, profileModule, registrationModule)
}

val supabaseModule = module {
    factory<SupabaseClient> { SupabaseManager.supabaseClient }
}

val settingsModule = module {
    factory<SettingsRepository> { SettingsRepositoryImpl() }
    single<SettingsViewModel> { SettingsViewModel(get()) }
}

val cardsModule = module {
    includes(supabaseModule)
    factory<CardsRepository> { CardsRepositoryImpl(get()) }
    factory<SwipeTracker> { SwipeTracker(Settings()) }
    single<CardsViewModel> { CardsViewModel(get(), get()) }
}

val chatModule = module {
    factory<ChatRepository> { ChatRepositoryImpl() }
    factory<FindRepository> { FindRepositoryImpl() }
    single<ChatViewModel> { ChatViewModel(get()) }
    single<FindViewModel> { FindViewModel() }
}

val profileModule = module {
    factory<ProfileRepository> { ProfileRepositoryImpl(get()) }
    single<ProfileViewModel> { ProfileViewModel(get()) }
}

val registrationModule = module {
    factory<UserRemoteDatasource> { UserRemoteDatasourceImpl() }
    factory<UserRepository> { UserRepositoryImpl(get()) }
    single<RegistrationViewModel> { RegistrationViewModel(get()) }
}