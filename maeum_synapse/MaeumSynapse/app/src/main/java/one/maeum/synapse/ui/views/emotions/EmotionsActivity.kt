package one.maeum.synapse.ui.views.emotions

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class EmotionsActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EmotionsScreen()
        }
    }

}