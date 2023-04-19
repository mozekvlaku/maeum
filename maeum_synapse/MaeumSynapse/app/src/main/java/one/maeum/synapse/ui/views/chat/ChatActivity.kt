package one.maeum.synapse.ui.views.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class ChatActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ChatScreen()
        }
    }

}