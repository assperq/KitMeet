package org.digital.kitmeet.notifications

import com.digital.chat.domain.FCMTokenRegistrar

object ServiceLocator {
    lateinit var tokenRegistrar: FCMTokenRegistrar

    fun initialize(registrar: FCMTokenRegistrar) {
        this.tokenRegistrar = registrar
    }

}