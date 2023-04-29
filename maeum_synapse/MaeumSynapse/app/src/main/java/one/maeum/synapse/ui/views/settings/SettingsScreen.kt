package one.maeum.synapse.ui.views.settings

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import one.maeum.synapse.MainActivity
import one.maeum.synapse.base.State
import one.maeum.synapse.matter.model.response.MatterState
import one.maeum.synapse.ui.AppContainerViewModel
import one.maeum.synapse.ui.views.home.HeaderPart
import one.maeum.synapse.ui.views.home.ObjectsCard
import one.maeum.synapse.ui.views.home.PeopleCard
import one.maeum.synapse.ui.views.home.StatusCard
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = getViewModel { parametersOf() },
    cstate:androidx.compose.runtime.State<MatterState>,
    state: androidx.compose.runtime.State<State>
) {

    val dialogShown = remember { mutableStateOf(false) }

    val activity = LocalContext.current as Activity


    if (dialogShown.value) {
        val ip = remember { mutableStateOf(viewModel.ipAddr.value) }
        val rest = remember { mutableStateOf(viewModel.restPort.value.toString()) }
        val ws = remember { mutableStateOf(viewModel.wsPort.value.toString()) }
        AlertDialog(
            buttons = {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            dialogShown.value = false
                            viewModel.setIP(ip.value)
                            viewModel.setRest(rest.value.toInt())
                            viewModel.setWs(ws.value.toInt())
                            viewModel.setAll()
                            reset(activity)
                        }
                    ) {
                        Text("Ok")
                    }
                }
            },
            title = {
                Text(text = "Nastavte IP Matteru")
            },
            text = {
                Column {
                    TextField(
                        value = ip.value,
                        onValueChange = { ip.value = it },
                        label = { Text("IP Adresa") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextField(
                        value = rest.value,
                        onValueChange = { rest.value = it },
                        label = { Text("REST Port") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextField(
                        value = ws.value,
                        onValueChange = { ws.value = it },
                        label = { Text("WebSocket Port") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            onDismissRequest = {
                dialogShown.value = false
            }
        )
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column {
            ListItem(
                headlineContent = { Text("Adresa Matter") },
                supportingContent = { Text(viewModel.ipAddr.value + ":" + viewModel.restPort.value + " a :" + viewModel.wsPort.value) },
                modifier = Modifier.clickable {
                      dialogShown.value = true
                },
                leadingContent = {
                    Icon(
                        Icons.Outlined.SendToMobile,
                        contentDescription = "Localized description",
                    )
                }
            )
            when (val result = state.value) {
                State.Loading -> {
                    androidx.compose.material.CircularProgressIndicator()
                }
                is State.Failure, State.None -> {
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {


                        Text(
                            text = "Další nastavení budou dostupná po připojení k Matter.",
                            modifier = Modifier
                                .align(alignment = Alignment.CenterHorizontally)
                                .padding(15.dp),
                            textAlign = TextAlign.Center
                        )


                    }
                }
                is State.Success -> {
                    settings(activity, cstate, viewModel)
                }
            }


        }
    }

}


fun reset(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    context.startActivity(intent)
}

@Composable
fun settings(activity: Activity, cstate: androidx.compose.runtime.State<MatterState>, viewModel: SettingsViewModel) {
    Divider()
    ListItem(
        headlineContent = { Text("Automatické mrkání") },
        supportingContent = { Text("Změní chování mrkání robota") },
        trailingContent = {
            cstate.value.vb?.blinking?.let { Switch(checked = it, onCheckedChange = {
                viewModel.changeBlinking(it)
            }) }
        },
        leadingContent = {
            Icon(
                Icons.Outlined.Visibility,
                contentDescription = "Localized description",
            )
        }
    )
    Divider()
    ListItem(
        headlineContent = { Text("Mimika") },
        supportingContent = { Text("Vypne / zapne mimiku") },
        trailingContent = {
            cstate.value.me?.doingMimics?.let {
                Switch(checked = it, onCheckedChange = {
                    viewModel.changeMimics(it)
                })
            }
        },
        leadingContent = {
            Icon(
                Icons.Outlined.Face,
                contentDescription = "Localized description",
            )
        }
    )
    Divider()
    ListItem(
        headlineContent = { Text("Rozhlížení") },
        supportingContent = { Text("Změní chování rozhlížení robota") },
        trailingContent = {
            cstate.value.le?.activatedLooking?.let { Switch(checked = it, onCheckedChange = {
                viewModel.changeLooking(it)
            }) }
        },
        leadingContent = {
            Icon(
                Icons.Outlined.RotateLeft,
                contentDescription = "Localized description",
            )
        }
    )
    Divider()
    ListItem(
        headlineContent = { Text("Verbální kortex") },
        supportingContent = { Text("Vypne / zapne verbální kortex") },
        trailingContent = {
            cstate.value.lt?.cortexSettings?.verbalCortexRasa?.useOnlyGPT?.let {
                Switch(checked = !it, onCheckedChange = {
                    viewModel.changeRasa(it)
                })
            }
        },
        leadingContent = {
            Icon(
                Icons.Outlined.RecordVoiceOver,
                contentDescription = "Localized description",
            )
        }
    )
    Divider()
    ListItem(
        headlineContent = { Text("Levá deska") },
        supportingContent = { Text("Vypne / zapne příkon levých servomotorů") },
        trailingContent = {
            cstate.value.mm?.nestor_state?.left?.let { Switch(checked = it, onCheckedChange = {
                viewModel.changeMotorsLeft(it)
            }) }
        },
        leadingContent = {
            Icon(
                Icons.Outlined.ForkLeft,
                contentDescription = "Localized description",
            )
        }
    )
    Divider()
    ListItem(
        headlineContent = { Text("Pravá deska") },
        supportingContent = { Text("Vypne / zapne příkon pravých servomotorů") },
        trailingContent = {
            cstate.value.mm?.nestor_state?.right?.let { Switch(checked = it, onCheckedChange = {
                viewModel.changeMotorsRight(it)
            }) }
        },
        leadingContent = {
            Icon(
                Icons.Outlined.ForkRight,
                contentDescription = "Localized description",
            )
        }
    )
}

fun items(function: () -> Unit) {

}
