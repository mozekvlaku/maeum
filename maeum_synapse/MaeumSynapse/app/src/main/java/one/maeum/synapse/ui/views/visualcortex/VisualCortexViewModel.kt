package one.maeum.synapse.ui.views.visualcortex

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import one.maeum.synapse.base.BaseViewModel
import one.maeum.synapse.base.State
import one.maeum.synapse.datastore.SynapseDataStore
import one.maeum.synapse.matter.repository.MatterRepository
import org.koin.core.KoinApplication.Companion.init

class VisualCortexViewModel (
    private val dataStore: SynapseDataStore
    ) : BaseViewModel() {

        var _ipAddr = MutableStateFlow<String>("false")
        var ipAddr = _ipAddr.asStateFlow()


        init {
            getSettings()
        }

        fun getSettings() {

            launch(

                block = {
                    _ipAddr.emit(dataStore.ipAddressFlow.first())
                    Log.d("dsf",dataStore.ipAddressFlow.first())
                    _state.emit(State.Success())
                }
            )
        }




    }