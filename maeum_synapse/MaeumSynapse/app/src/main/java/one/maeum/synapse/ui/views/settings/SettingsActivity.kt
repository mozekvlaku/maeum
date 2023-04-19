package one.maeum.synapse.ui.views.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class SettingsActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SettingsActivity()
        }
    }

}