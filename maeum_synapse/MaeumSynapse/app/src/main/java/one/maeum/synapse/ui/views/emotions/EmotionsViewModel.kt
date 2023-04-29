package one.maeum.synapse.ui.views.emotions

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import one.maeum.synapse.base.BaseViewModel
import one.maeum.synapse.matter.repository.MatterRepository

class EmotionsViewModel(
    private val matterRepository: MatterRepository
): BaseViewModel() {

    fun sendEmotion(value: String) = launch(
        block = {
            matterRepository.sendEmotion(value)
        }
    )
}