package one.maeum.synapse.ui.views.visualcortex

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import one.maeum.synapse.R
import one.maeum.synapse.ui.views.home.HeaderPart
import one.maeum.synapse.ui.views.home.ObjectsCard
import one.maeum.synapse.ui.views.home.PeopleCard
import one.maeum.synapse.ui.views.home.StatusCard

@Preview
@Composable
fun VisualCortexScreen() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.matterload),
                contentDescription = "Načítání",
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}
