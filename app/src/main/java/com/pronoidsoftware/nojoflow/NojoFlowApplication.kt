package com.pronoidsoftware.nojoflow

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class NojoFlowApplication: Application() {
    val dataStore: DataStore<Preferences> by preferencesDataStore("settings")
    val applicationScope = CoroutineScope(SupervisorJob())
}