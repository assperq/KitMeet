package org.digital.kitmeet.di

import com.digital.chat.presentation.ChatViewModel
import com.digital.chat.presentation.ui.find.FindViewModel
import com.digital.registration.presentation.RegistrationViewModel
import com.digital.settings.presentation.SettingsViewModel
import com.example.cardss.CardsViewModel
import com.example.cardss.data.CardsRepository
import com.example.profile.presentation.ProfileViewModel
import org.koin.core.Koin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

object AppViewModels: KoinComponent {
    fun getSettingsViewModel() = get<SettingsViewModel>()
    fun getCardsViewModel() = get<CardsViewModel>()
    fun getChatViewModel() = get<ChatViewModel>()
    fun getFindViewModel() = get<FindViewModel>()
    fun getProfileViewModel() = get<ProfileViewModel>()
    fun getRegistrationViewModel() = get<RegistrationViewModel>()
}