package com.digital.settings.domain

data class Settings(
    val enablePush : Boolean,
    val theme : Theme,
    val email : String,
    val password : String
)
