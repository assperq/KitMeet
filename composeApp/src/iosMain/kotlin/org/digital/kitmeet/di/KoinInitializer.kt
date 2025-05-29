package org.digital.kitmeet.di

import org.koin.core.context.startKoin

actual class KoinInitializer {
    actual fun init() {
        startKoin {
            modules(supabaseModule, settingsModule, cardsModule, chatModule, profileModule, registrationModule)
        }
    }
}