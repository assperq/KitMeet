package com.digital.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlin.jvm.JvmStatic

object DataStoreManager {
    @JvmStatic
    val dataStore : DataStore<Preferences> = createDataStore()

    val dataStoreFileName = "kitmeet.preferences_pb"
}

internal expect fun createDataStore(): DataStore<Preferences>