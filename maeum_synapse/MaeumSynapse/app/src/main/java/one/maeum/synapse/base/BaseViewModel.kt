package one.maeum.synapse.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel : ViewModel() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private val _state = MutableStateFlow<State>(State.None)
    val state = _state.asStateFlow()

    protected fun <Result> launch(
        onError: ((Throwable) -> Unit)? = null,
        state: MutableStateFlow<State>? = _state,
        block: (suspend CoroutineScope.() -> Result)
    ) = scope.launch(throwableHandler<Result>(onError, state)) {

        // Zobrazi loading na obrazovce
        state?.emit(State.Loading)

        // Provola operaci z vstupnuho atributu block (napr. API volani)
        val result = block()

        // Operace uspesne dokoncena, vracime Success a data
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