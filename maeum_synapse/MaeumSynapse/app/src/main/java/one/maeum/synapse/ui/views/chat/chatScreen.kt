package one.maeum.synapse.ui.views.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import one.maeum.synapse.R
import one.maeum.synapse.base.BaseViewModel
import one.maeum.synapse.matter.model.response.MatterState
import one.maeum.synapse.matter.model.response.Message
import one.maeum.synapse.matter.service.MatterService
import one.maeum.synapse.ui.AppContainerViewModel
import one.maeum.synapse.ui.theme.MaeumSynapseTheme
import one.maeum.synapse.ui.views.home.HomeViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun ChatScreen(viewModel: AppContainerViewModel = getViewModel(), cstate:androidx.compose.runtime.State<MatterState>) {
    var msgs: List<Message>? = cstate.value.vr?.message_history?.reversed()
    var messageText = remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize()) {
        ChatHistory(msgs)

        Row(modifier = Modifier.width(330.dp)) {
            OutlinedTextField(
                value = messageText.value,
                onValueChange = { messageText.value = it },
                placeholder = { Text("Napište mi") },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (messageText.value != "") {
                                viewModel.sendToAI(messageText.value)
                                messageText.value = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send")
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            )

        }
    }


}

@Composable
fun ChatHistory(msgs: List<Message>?)
{
    LazyColumn(reverseLayout = true, modifier = Modifier.height(651.dp)) {
        if (msgs != null) {
            items(msgs.size) { index ->
                MessageBubble(msgs[index])
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    Row(
        modifier = Modifier.padding(8.dp).fillMaxWidth(),
        horizontalArrangement = if (message.myself) Arrangement.End else Arrangement.Start
    ) {
        if (message.myself) {


            Spacer(modifier = Modifier.width(8.dp))
        }
        if (!message.myself) {
            ProfilePicture(modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Card(
            backgroundColor = if (message.myself) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary,
            elevation = 2.dp,
            shape = RoundedCornerShape(13.dp),

            modifier = Modifier.width(190.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = message.content, color = if (message.myself) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = (message.timestamp.hour.toString() + ":" + message.timestamp.minute.toString()), color = if (message.myself) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary)
            }
        }
        if (message.myself) {

            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun ProfilePicture(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.emotion_curious),
        contentDescription = null,
        modifier = modifier.clip(CircleShape)
    )
}
