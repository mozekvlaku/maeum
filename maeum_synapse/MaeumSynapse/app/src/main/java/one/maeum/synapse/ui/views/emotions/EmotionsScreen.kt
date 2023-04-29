package one.maeum.synapse.ui.views.emotions

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import one.maeum.synapse.R
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Preview
@Composable
fun EmotionsScreen(viewModel: EmotionsViewModel = getViewModel { parametersOf() }) {
    val texts = listOf(
        "Neutral",
        "Happy",
        "Sad",
        "Angry",
        "Curious",
        "Disgusted",
        "Fearful",
        "Suspicious",
        "Surprised",
        "Sleepy"
    )

    val textsNames = listOf(
        "Pohoda",
        "Radost",
        "Smutek",
        "Vztek",
        "Zvědavost",
        "Znechucení",
        "Děs",
        "Podezřívání",
        "Překvapení",
        "Spánek"
    )

    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(texts.size) { index ->
            Card(
                modifier = Modifier
                    .padding(9.dp)
                    .clickable { viewModel.sendEmotion(texts[index]) },
                elevation = 8.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomStart,

                    ) {

                    Image(
                        painter = painterResource(id = getDrawableId(texts[index])),
                        contentDescription = null,
                        alpha = 0.7F,
                        modifier = Modifier
                            .size(90.dp)
                            .padding(top = 8.dp, end = 0.dp, start = 0.dp, bottom = 0.dp)
                    )
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomEnd,

                        ) {
                    Text(
                        text = textsNames[index],
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )}
                }
            }
        }
    }
}

fun getDrawableId(name: String): Int {
    val resourceName = name.lowercase()
    return when (resourceName) {
        "neutral" -> R.drawable.emotion_neutral
        "happy" -> R.drawable.emotion_happy
        "sad" -> R.drawable.emotion_sad
        "angry" -> R.drawable.emotion_angry
        "curious" -> R.drawable.emotion_curious
        "disgusted" -> R.drawable.emotion_disgusted
        "fearful" -> R.drawable.emotion_fearful
        "suspicious" -> R.drawable.emotion_suspicious
        "surprised" -> R.drawable.emotion_surprised
        "sleepy" -> R.drawable.emotion_sleepy
        else -> R.drawable.emotion_neutral // fallback image
    }
}