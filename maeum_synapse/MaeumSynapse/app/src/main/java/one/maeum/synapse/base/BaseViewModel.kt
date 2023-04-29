package one.maeum.synapse.base

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import one.maeum.synapse.datastore.SynapseDataStore
import one.maeum.synapse.matter.model.response.MatterState
import org.json.JSONObject

abstract class BaseViewModel : ViewModel() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    var _state = MutableStateFlow<State>(State.None)
    var state = _state.asStateFlow()


    private val _matterState = MutableStateFlow<MatterState?>(null)
    var matterState = _matterState.asStateFlow()





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