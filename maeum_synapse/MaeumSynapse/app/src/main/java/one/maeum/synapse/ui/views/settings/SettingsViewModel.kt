package one.maeum.synapse.ui.views.settings

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.*
import one.maeum.synapse.base.BaseViewModel
import one.maeum.synapse.base.State
import one.maeum.synapse.datastore.SynapseDataStore
import one.maeum.synapse.matter.repository.MatterRepository

class SettingsViewModel (
    private val matterRepository: MatterRepository,
    private val dataStore: SynapseDataStore
) : BaseViewModel() {

    private var _ipAddr = MutableStateFlow<String>("false")
    var ipAddr = _ipAddr.asStateFlow()
    private var _restPort = MutableStateFlow<Int>(3000)
    var restPort = _restPort.asStateFlow()
    private var _wsPort = MutableStateFlow<Int>(3001)
    var wsPort = _wsPort.asStateFlow()

    private var _loaded = MutableStateFlow<Boolean>(false)
    var loaded = _loaded.asStateFlow()


    init {
        getSettings()
    }

    fun getSettings() {

        launch(
            block = {
                _ipAddr.emit(dataStore.ipAddressFlow.first())
                _restPort.emit(dataStore.portRestFlow.first())
                _wsPort.emit(dataStore.portWsFlow.first())
                _loaded.emit(true)
            }
        )
    }

    fun setIP(ip:String) = launch(block={ _ipAddr.emit(ip) })
    fun setRest(rest:Int) = launch(block={ _restPort.emit(rest) })
    fun setWs(ws:Int) = launch(block={ _wsPort.emit(ws) })

    fun setAll() {
        launch(block={
            dataStore.saveIpAddress(ipAddr.value)
            dataStore.savePortRest(restPort.value)
            dataStore.savePortWs(wsPort.value)
        })
    }

    fun changeMimics(value:Boolean) = launch(
        block = {
            matterRepository.mimics(value)
        }
    )

    fun changeLooking(value: Boolean) = launch(
        block = {
            matterRepository.auto(value)
        }
    )

    fun changeMotorsLeft(value: Boolean) = launch(
        block = {
            matterRepository.left(value)
        }
    )

    fun changeMotorsRight(value: Boolean) = launch(
        block = {
            matterRepository.right(value)
        }
    )

    fun changeBlinking(value:Boolean) {

            launch(
                block = {
                    matterRepository.blinking(value)
                }
            )

    }

    fun changeRasa(value:Boolean) {

            launch(
                block = {
                    matterRepository.rasa(value)
                }
            )

    }

}
