package one.maeum.synapse.ui

import android.util.Log
import one.maeum.synapse.base.BaseViewModel
import one.maeum.synapse.matter.repository.MatterRepository

class AppContainerViewModel(
    private val matterRepository: MatterRepository
): BaseViewModel() {
     fun sendToAI(text:String)  = launch(
         block = {
             matterRepository.sendTextToAI(text)?.execute()
         }
     )
}