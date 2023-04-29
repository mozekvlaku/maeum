package one.maeum.synapse.ui

import android.util.Log
import androidx.compose.runtime.collectAsState
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import one.maeum.synapse.base.BaseViewModel
import one.maeum.synapse.matter.repository.MatterRepository
import one.maeum.synapse.matter.repository.MatterSocketRepository

class AppContainerViewModel(
    private val matterRepository: MatterRepository,
    private val matterSocketRepository: MatterSocketRepository
): BaseViewModel() {

    init {

        Log.d("SocketSynapse", "InitViewModel.source")
        matterSocketRepository.initSocket(this)
    }

     fun sendToAI(text:String)  = launch(
         block = {
             matterRepository.sendTextToAI(text)?.execute()
         }
     )

    fun emergency() = launch(
        block = {
            matterRepository.right(false)
            matterRepository.left(false)
        }
    )

}