package one.maeum.synapse.matter.repository

import android.util.Log
import one.maeum.synapse.matter.model.response.MatterActionResponse
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

    suspend fun sendEmotion(value: String): MatterActionResponse? = matterService.setEmotions(value)

    suspend fun getState(): MatterStateResponse? = matterService.getState()

    suspend fun blinking(onOff: Boolean): MatterActionResponse? = if (onOff) matterService.startBlinking() else matterService.stopBlinking()

    suspend fun mimics(onOff: Boolean): MatterActionResponse? = if (onOff) matterService.startMimics() else matterService.stopMimics()

    suspend fun rasa(onOff: Boolean): MatterActionResponse? = if (onOff) matterService.startRasa() else matterService.stopRasa()

    suspend fun auto(onOff: Boolean): MatterActionResponse? = if (onOff) matterService.startAuto() else matterService.stopAuto()

    suspend fun left(onOff: Boolean): MatterActionResponse? = if (onOff) matterService.leftOn() else matterService.leftOff()

    suspend fun right(onOff: Boolean): MatterActionResponse? = if (onOff) matterService.rightOn() else matterService.rightOff()

}