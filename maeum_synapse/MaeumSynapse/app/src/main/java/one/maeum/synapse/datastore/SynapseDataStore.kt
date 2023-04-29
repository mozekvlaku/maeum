package one.maeum.synapse.datastore

import android.app.Application
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private val SYNAPSE_PREFS =  "synapse_datastore_prefs"


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SYNAPSE_PREFS)

object SynapseDataStore: KoinComponent {
    private val ipKey = stringPreferencesKey("ip_address")
    private val portRestKey = intPreferencesKey("port_rest")
    private val portWsKey = intPreferencesKey("port_ws")

    private val app: Application by inject()

    private val dataStore = app.dataStore

    suspend fun saveIpAddress(ip: String) {
        dataStore.edit { settings ->
            settings[ipKey] = ip
        }

    }

    suspend fun savePortRest(port_rest: Int) {
        dataStore.edit { settings ->
            settings[portRestKey] = port_rest
        }
    }

    suspend fun savePortWs(port_ws: Int) {
        dataStore.edit { settings ->
            settings[portWsKey] = port_ws
        }
    }

    private val _ipAddressFlow: Flow<String> by lazy {
        dataStore.data.map { settings ->
            settings[ipKey] ?: "192.168.102.171"
        }
    }
    val ipAddressFlow: Flow<String>
        get() = _ipAddressFlow

    private val _portRestFlow: Flow<Int> by lazy {
        dataStore.data.map { settings ->
            settings[portRestKey] ?: 3000
        }
    }
    val portRestFlow: Flow<Int>
        get() = _portRestFlow

    private val _portWsFlow: Flow<Int> by lazy {
        dataStore.data.map { settings ->
            settings[portWsKey] ?: 3001
        }
    }
    val portWsFlow: Flow<Int>
        get() = _portWsFlow
}