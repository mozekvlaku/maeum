package one.maeum.synapse.matter.repository

import android.util.Log
import one.maeum.synapse.matter.model.response.MatterState
import one.maeum.synapse.matter.model.response.MatterStateResponse
import one.maeum.synapse.matter.model.response.MatterVerbalOutput
import one.maeum.synapse.matter.service.MatterService
import retrofit2.Call

class MatterRepository (
    private val matterService: MatterService) : BaseRepository() {
    suspend fun sendTextToAI(text:String): Call<MatterVerbalOutput>? {
        Log.d("TAG", "Sending to Matter: " + text)
        return matterService.sendTextToAI(text)
    }

    suspend fun getState(): MatterStateResponse? = matterService.getState()
}