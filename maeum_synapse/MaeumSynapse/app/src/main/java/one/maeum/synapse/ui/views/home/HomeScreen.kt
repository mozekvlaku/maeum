package one.maeum.synapse.ui.views.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import one.maeum.synapse.R
import one.maeum.synapse.matter.model.response.Obj
import org.koin.androidx.compose.getViewModel
import one.maeum.synapse.base.State
import one.maeum.synapse.matter.enum.EmotionsEnum
import one.maeum.synapse.matter.model.response.MatterState
import one.maeum.synapse.matter.model.response.Person

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = getViewModel(), cstate:androidx.compose.runtime.State<MatterState>, state: androidx.compose.runtime.State<State>) {

    when (val result = state.value) {
        State.Loading -> {
            androidx.compose.material.CircularProgressIndicator()
        }
        is State.Failure, State.None -> {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.deadbot),
                    contentDescription = "Není možné připojit se k Matter",
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                        .width(190.dp)
                )
                Text(
                    text = "Matter je offline!",
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                        .padding(top = 15.dp, bottom = 1.dp),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                androidx.compose.material.Text(
                    text = "Nebylo možné se připojit k Maeum Matter. Připojení bude obnoveno automaticky.",
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally).padding(15.dp),
                    textAlign = TextAlign.Center
                )


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
                        PeopleCard(cstate)
                    }
                }
            }
        }
    }

}

@Composable
fun HeaderPart(state: androidx.compose.runtime.State<MatterState?>) {
    val context = LocalContext.current
    var robotFace = R.drawable.emotion_sleepy

    when (state.value?.ep?.em_state) {
        "Neutral" -> robotFace = R.drawable.emotion_neutral
        "Happy" -> robotFace = R.drawable.emotion_happy
        "Sad" -> robotFace = R.drawable.emotion_sad
        "Angry" -> robotFace = R.drawable.emotion_angry
        "Curious" -> robotFace = R.drawable.emotion_curious
        "Disgusted" -> robotFace = R.drawable.emotion_disgusted
        "Fearful" -> robotFace = R.drawable.emotion_fearful
        "Suspicious" -> robotFace = R.drawable.emotion_suspicious
        "Surprised" -> robotFace = R.drawable.emotion_surprised
        else -> robotFace = R.drawable.emotion_sleepy
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = robotFace),
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
            state.value?.ep?.em_state_int?.let {
                var emotionString:String = context.getString(EmotionsEnum.values()[it].nameRes)
                Text(
                    text = emotionString,
                    fontWeight = FontWeight.Black,
                    fontSize = 38.sp,
                    lineHeight = 38.sp
                )
            }

        }
    }
}

@Composable
fun ObjectsCard(state: androidx.compose.runtime.State<MatterState?>)
{
    val list: List<Obj>? = state.value?.vm?.memory?.objects



    Card(modifier = Modifier
        .fillMaxWidth(0.5f)
        .fillMaxHeight(1f)) {
        Column(Modifier.padding(top = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Objekty",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 18.sp)
            if (list != null) {
                if(list.size > 0) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(list) { objInView ->
                            ListItem(
                                headlineContent = { Text(objInView.count.toString() + "x " +objInView.name) },
                                supportingContent = { Text("J: "+objInView.probability+"%, E: " + objInView.relation) },
                                colors = ListItemDefaults.colors(
                                    containerColor = Color.Transparent
                                )
                            )
                            Divider()
                        }
                    }
                } else {
                    Image(painter = painterResource(id = R.drawable.nothing), modifier = Modifier.width(50.dp).padding(top=100.dp, bottom=7.dp), alpha = 0.5f, contentDescription = "Nic")
                    Text(text="Nic nevidím", fontSize = 12.sp, color = Color.Gray)
                }
            }

        }
    }
}

@Composable
fun PeopleCard(state: androidx.compose.runtime.State<MatterState?>)
{
    val list: List<Person>? = state.value?.vm?.memory?.people
    val countpeople = state.value?.vm?.view?.people?.size
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
        if (list != null) {
            if (countpeople != null) {
                if((list.size > 0) && (countpeople > 0)) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(list) { objInView ->
                            var fontWeight = FontWeight.Normal
                            var nameArr = objInView.name.split(" ");

                            if(objInView.foreground)
                                fontWeight = FontWeight.Bold

                            if(objInView.inView) {
                                ListItem(
                                    headlineContent = { Text(objInView.name, fontWeight = fontWeight) },
                                    supportingContent = { Text(objInView.emotion_current) },
                                    colors = ListItemDefaults.colors(
                                        containerColor = Color.Transparent
                                    ),
                                    leadingContent = {
                                        FilledIconButton(onClick = { /* doSomething() */ }) {
                                            Text(text = nameArr[0][0].toString()+nameArr[1][0].toString())
                                        }
                                    }
                                )
                                Divider()
                            }

                        }
                    }
                } else {
                    Image(painter = painterResource(id = R.drawable.nothing), modifier = Modifier.width(50.dp).align(alignment = Alignment.CenterHorizontally).padding(top=88.dp, bottom=7.dp), alpha = 0.5f, contentDescription = "Nic")
                    Text(text="Nikoho nevidím", fontSize = 12.sp, modifier=Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Color.Gray)
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusCard(state: androidx.compose.runtime.State<MatterState?>) {
    var nestorState = state.value?.mm?.nestor_online
    var visualState = state.value?.vp?.visualOnline
    var verbalState = state.value?.vr?.rasa_online
    var ttsState = state.value?.vr?.tts_online


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
                var contColor = MaterialTheme.colorScheme.error
                var textMsg = "offline"
                if(visualState == true)
                {
                    contColor = MaterialTheme.colorScheme.primary
                    textMsg = "online"
                }
                BadgedBox(
                    modifier = Modifier.padding(bottom = 5.dp),
                    badge = {
                        Badge (containerColor = contColor) {

                        }
                    }) {
                    Icon(Icons.Outlined.Visibility, contentDescription = "Localized description", tint = MaterialTheme.colorScheme.onSurface)
                }
                Text("Visual",
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp)
                Text(textMsg,
                    fontWeight = FontWeight.Normal,
                    fontSize = 9.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var contColor = MaterialTheme.colorScheme.error
                var textMsg = "offline"
                if(verbalState == true)
                {
                    contColor = MaterialTheme.colorScheme.primary
                    textMsg = "online"
                }
                BadgedBox(
                    modifier = Modifier.padding(bottom = 5.dp),
                    badge = {
                        Badge (containerColor = contColor) {

                        }
                    }) {
                    Icon(Icons.Outlined.RecordVoiceOver, contentDescription = "Localized description", tint = MaterialTheme.colorScheme.onSurface)
                }
                Text("Verbal",
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp)
                Text(textMsg,
                    fontWeight = FontWeight.Normal,
                    fontSize = 9.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var contColor = MaterialTheme.colorScheme.error
                var textMsg = "offline"
                if(nestorState == true)
                {
                    contColor = MaterialTheme.colorScheme.primary
                    textMsg = "online"
                }
                BadgedBox(
                    modifier = Modifier.padding(bottom = 5.dp),
                    badge = {
                        Badge (containerColor = contColor) {

                        }
                    }) {
                    Icon(Icons.Outlined.SentimentSatisfied, contentDescription = "Localized description", tint = MaterialTheme.colorScheme.onSurface)
                }
                Text("Nestor",
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp)
                Text(textMsg,
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
                var contColor = MaterialTheme.colorScheme.error
                var textMsg = "offline"
                if(ttsState == true)
                {
                    contColor = MaterialTheme.colorScheme.primary
                    textMsg = "online"
                }
                BadgedBox(
                    modifier = Modifier.padding(bottom = 5.dp),
                    badge = {
                        Badge (containerColor = contColor) {

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
                Text(textMsg,
                    fontWeight = FontWeight.Normal,
                    fontSize = 9.sp)
            }
        }
    }
}