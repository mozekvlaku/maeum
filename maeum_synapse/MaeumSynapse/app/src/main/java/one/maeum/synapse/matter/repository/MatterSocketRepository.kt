package one.maeum.synapse.matter.repository

import android.util.Log
import androidx.datastore.core.DataStore
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import one.maeum.synapse.base.BaseViewModel
import one.maeum.synapse.base.State
import one.maeum.synapse.datastore.SynapseDataStore
import one.maeum.synapse.matter.model.response.MatterState
import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.prefs.Preferences

class MatterSocketRepository() : KoinComponent {

    private val datastore: SynapseDataStore by inject()
    private val _matterState: MutableStateFlow<MatterState?> = MutableStateFlow(null)
    private val _state: MutableStateFlow<State> = MutableStateFlow(State.None)

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private lateinit var socket: Socket



    suspend fun connectToSocket(callback:()->Unit) {
        val ipAddress = this.datastore.ipAddressFlow.first()
        val ws = this.datastore.portWsFlow.first()
        Log.d("SocketSynapse", "Připojuji k " + ipAddress)
        val options = IO.Options().apply {
            forceNew = true
            query = "ip=$ipAddress"
        }

        socket = IO.socket("http://$ipAddress:$ws", options)

        socket.on(Socket.EVENT_CONNECT) {
            callback()
        }

        socket.connect()


    }

    fun initSocket(viewModel: BaseViewModel) {

        Log.d("SocketSynapse", "Init")
        launch(block = {

            Log.d("SocketSynapse", "Launched")
            connectToSocket() {
                Log.d("SocketSynapse", "Subscribing")

                subscribe(viewModel)
            }

        })
    }

    fun subscribe(viewModel: BaseViewModel) {
        viewModel.matterState = _matterState
        viewModel.state = _state
        val mSocket = socket;

        Log.d("SocketSynapse", "Subscribed")
        mSocket.on("disconnect")  { args ->
            if (args[0] != null) {
                launch(
                    showLoad = false,
                    showSuccess = false,
                    block = {
                        _state.emit(State.Failure(null))
                    }
                )
            }
        }
        mSocket.on("state")  { args ->
            if (args[0] != null) {
                val counter: JSONObject = args[0] as JSONObject
                var gson = Gson()
                var mMEnt = gson?.fromJson(counter.toString(), MatterState::class.java)
                launch(
                    showLoad = false,
                    block = {
                        if (mMEnt != null) {
                            _matterState.emit(mMEnt)
                            _state.emit(State.Success())
                        }
                    }
                )
            }
        }
    }

    fun disconnectFromSocket() {
        if (::socket.isInitialized && socket.connected()) {
            socket.disconnect()
        }
    }
    protected fun <Result> launch(
        onError: ((Throwable) -> Unit)? = null,
        state: MutableStateFlow<State>? = _state,
        block: (suspend CoroutineScope.() -> Result),
        showLoad: Boolean = true,
        showSuccess: Boolean = true
    ) = scope.launch(throwableHandler<Result>(onError, state)) {

        // Zobrazi loading na obrazovce
        if(showLoad)
            state?.emit(State.Loading)

        // Provola operaci z vstupnuho atributu block (napr. API volani)
        val result = block()

        // Operace uspesne dokoncena, vracime Success a data
        if(showSuccess)
            state?.emit(State.Success(result))
    }



    private fun <Result> throwableHandler(
        onError: ((Throwable) -> Unit)? = null,
        state: MutableStateFlow<State>?,
    ) = CoroutineExceptionHandler { _, throwable ->
        onError?.invoke(throwable)
        state?.tryEmit(
            State.Failure(throwable)
        )
    }

}