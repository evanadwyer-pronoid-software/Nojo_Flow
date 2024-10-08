package com.pronoidsoftware.nojoflow.data

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.pronoidsoftware.nojoflow.domain.PreferencesConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DNDPolicyChangeReceiver : BroadcastReceiver() {

    @Inject
    lateinit var dataStore: DataStore<Preferences>

    @Inject
    lateinit var applicationScope: CoroutineScope

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.app.action.NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED") {
            applicationScope.launch {
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                dataStore.edit {
                    it[booleanPreferencesKey(PreferencesConstants.AUTO_DND_ENABLED)] =
                        notificationManager.isNotificationPolicyAccessGranted
                }
            }
        }
    }
}