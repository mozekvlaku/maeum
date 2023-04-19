package one.maeum.synapse.ui.views.home

import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import one.maeum.synapse.R
import one.maeum.synapse.matter.model.response.MatterStateResponse
import one.maeum.synapse.matter.model.response.Obj
import org.koin.androidx.compose.getViewModel
import one.maeum.synapse.base.State

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun HomeScreen(viewModel: HomeViewModel = getViewModel()) {
    val cstate = viewModel.stateGottenGoneGottenNow.collectAsState()
    val state = viewModel.state.collectAsState()
    when (val result = state.value) {
        State.None, State.Loading -> {
            androidx.compose.material.CircularProgressIndicator()
        }
        is State.Failure -> {
            Column {
                androidx.compose.material.Text(text = "Chyba - ${result.throwable.localizedMessage}")
                androidx.compose.material.Button(onClick = { viewModel.fetchState() }) {
                    androidx.compose.material.Text("Zkusit znovu")
                }
            }
        }
        is State.Success -> {
            if (cstate == null) {
                androidx.compose.material.Text(text = "Data nejsou dostupna")
            } else {
                Column(modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()) {
                    HeaderPart(cstate)
                    StatusCard(cstate)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center) {
                        ObjectsCard(cstate)
                        Spacer(modifier = Modifier.width(16.dp))
                        PeopleCard()
                    }
                }
            }
        }
    }

}

@Composable
fun HeaderPart(state: androidx.compose.runtime.State<MatterStateResponse>) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.happy_emotion),
            contentDescription = "My Image",
            modifier = Modifier
                .size(200.dp)
                .padding(end = 34.dp)
        )
        Column (
            Modifier
                .align(Alignment.Bottom)
                .padding(bottom = 16.dp)
        ) {
            state.value.result?.ep?.em_state?.let {
                Text(
                    text = it,
                    fontWeight = FontWeight.Black,
                    fontSize = 38.sp,
                    lineHeight = 38.sp
                )
            }

        }
    }
}

@Composable
fun ObjectsCard(state: androidx.compose.runtime.State<MatterStateResponse>)
{
    val list: List<Obj>? = state.value.result?.vm?.memory?.objects
    Card(modifier = Modifier
        .fillMaxWidth(0.5f)
        .fillMaxHeight(1f)) {
        Column(Modifier.padding(top = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Objekty",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 18.sp)
            if(list !== null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(list) { objInView ->
                        ListItem(
                            headlineContent = { Text(objInView.name) },
                            supportingContent = { Text("J: 80%, P: 1") },
                            colors = ListItemDefaults.colors(
                                containerColor = Color.Transparent
                            ),
                            leadingContent = {
                                androidx.compose.material3.Icon(
                                    Icons.Outlined.Person,
                                    contentDescription = "Localized description",
                                )
                            }
                        )
                        Divider()
                    }
                }
            }
            ListItem(
                headlineContent = { Text("Osoba") },
                supportingContent = { Text("J: 80%, P: 1") },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent
                ),
                leadingContent = {
                    androidx.compose.material3.Icon(
                        Icons.Outlined.Person,
                        contentDescription = "Localized description",
                    )
                }
            )
            Divider()
            ListItem(
                headlineContent = { Text("Květina") },
                supportingContent = { Text("J: 40%, P: 3") },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent
                ),
                leadingContent = {
                    androidx.compose.material3.Icon(
                        Icons.Outlined.Nature,
                        contentDescription = "Localized description",
                    )
                }
            )
        }
    }
}

@Composable
fun PeopleCard()
{
    Card(modifier = Modifier
        .fillMaxWidth(1f)
        .fillMaxHeight(1f)) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Lidé",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 18.sp)
        }
        ListItem(
            headlineContent = { Text("Tomáš Kracík", fontWeight = FontWeight.Bold) },
            supportingContent = { Text("Cítí se dobře") },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            ),
            leadingContent = {
                FilledIconButton(onClick = { /* doSomething() */ }) {
                    Text(text = "TK")
                }
            }
        )
        Divider()
        ListItem(
            headlineContent = { Text("Matouš Kracík", fontWeight = FontWeight.Normal) },
            supportingContent = { Text("Bojí se") },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            ),
            leadingContent = {
                FilledIconButton(onClick = { /* doSomething() */ }) {
                    Text(text = "MK")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusCard(state: androidx.compose.runtime.State<MatterStateResponse>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .size(width = 50.dp, height = 105.dp)
    ) {
        Row(
            modifier = Modifier.padding(22.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BadgedBox(
                    modifier = Modifier.padding(bottom = 5.dp),
                    badge = {
                        Badge (containerColor = MaterialTheme.colorScheme.primary) {

                        }
                    }) {
                    Icon(Icons.Outlined.Visibility, contentDescription = "Localized description", tint = MaterialTheme.colorScheme.onSurface)
                }
                Text("Visual",
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp)
                Text("online",
                    fontWeight = FontWeight.Normal,
                    fontSize = 9.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BadgedBox(
                    modifier = Modifier.padding(bottom = 5.dp),
                    badge = {
                        Badge (containerColor = MaterialTheme.colorScheme.error) {

                        }
                    }) {
                    Icon(Icons.Outlined.RecordVoiceOver, contentDescription = "Localized description", tint = MaterialTheme.colorScheme.onSurface)
                }
                Text("Verbal",
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp)
                Text("offline",
                    fontWeight = FontWeight.Normal,
                    fontSize = 9.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BadgedBox(
                    modifier = Modifier.padding(bottom = 5.dp),
                    badge = {
                        Badge (containerColor = MaterialTheme.colorScheme.primary) {

                        }
                    }) {
                    Icon(Icons.Outlined.SentimentSatisfied, contentDescription = "Localized description", tint = MaterialTheme.colorScheme.onSurface)
                }
                Text("Nestor",
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp)
                Text("offline",
                    fontWeight = FontWeight.Normal,
                    fontSize = 9.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BadgedBox(
                    modifier = Modifier.padding(bottom = 5.dp),
                    badge = {
                        Badge (containerColor = MaterialTheme.colorScheme.primary) {

                        }
                    }) {
                    Icon(Icons.Outlined.Psychology, contentDescription = "Localized description", tint = MaterialTheme.colorScheme.onSurface)
                }
                Text("Matter",
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp)
                Text("online",
                    fontWeight = FontWeight.Normal,
                    fontSize = 9.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BadgedBox(
                    modifier = Modifier.padding(bottom = 5.dp),
                    badge = {
                        Badge (containerColor = MaterialTheme.colorScheme.error) {

                        }
                    }) {
                    Icon(
                        Icons.Outlined.SpeakerNotes,
                        contentDescription = "Favorite", tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text("TTS",

                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp)
                Text("offline",
                    fontWeight = FontWeight.Normal,
                    fontSize = 9.sp)
            }
        }
    }
}