package com.digital.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.digital.settings.AndroidContextWrapper

actual fun createDataStore(): DataStore<Preferences> {
    val context = AndroidContextWrapper.applicationContext
    return PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile(DataStoreManager.dataStoreFileName) }
    )
}