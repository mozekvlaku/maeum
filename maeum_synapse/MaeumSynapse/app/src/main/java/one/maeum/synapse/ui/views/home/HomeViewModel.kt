package one.maeum.synapse.ui.views.home

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import one.maeum.synapse.base.BaseViewModel
import one.maeum.synapse.matter.model.response.MatterStateResponse
import one.maeum.synapse.matter.repository.MatterRepository

class HomeViewModel(
    private val matterRepository: MatterRepository
): BaseViewModel() {

    private val stateGotten = MutableStateFlow<MatterStateResponse>(MatterStateResponse("", 1, null))
    val stateGottenGoneGottenNow = stateGotten.asStateFlow()

    init {
        fetchState()
    }

    fun fetchState() = launch(
        block = {
            matterRepository.getState().also {
                if (it != null) {
                    stateGotten.emit(it)
                }
            }

            // Ekvivalent kodu vyse
//            val data = spaceXRepository.fetchAllSuccessfulLaunches()
//            _successRocketLaunches.emit(data)
//            return@launch data
        }
    )

}