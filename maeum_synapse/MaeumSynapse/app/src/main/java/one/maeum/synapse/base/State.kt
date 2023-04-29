package one.maeum.synapse.base

sealed interface State {

    object None: State

    object Loading: State

    class Success(val any: Any? = null): State

    class Failure(
        val throwable: Throwable?
    ): State

}