package one.maeum.synapse.ui.views.visualcortex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class VisualCortexActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VisualCortexScreen()
        }
    }

}