package com.digital.settings

import android.content.Context

object AndroidContextWrapper {
    lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }
}