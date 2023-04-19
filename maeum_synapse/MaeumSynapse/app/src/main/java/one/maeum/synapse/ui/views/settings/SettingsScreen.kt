package one.maeum.synapse.ui.views.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Card(
                onClick = { /* Do something */ },
                modifier = Modifier.size(width = 180.dp, height = 100.dp)
            ) {
                Box(Modifier.fillMaxSize()) {
                    Text("Clickable", Modifier.align(Alignment.Center))
                }
            }
            Card () {
                ListItem(
                    headlineContent = { Text("Maeum Matter") },
                    leadingContent = {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = "Localized description",
                        )
                    }
                )
            }


            ListItem(
                headlineContent = { Text("Automatické mrkání") },
                supportingContent = { Text("Změní chování mrkání robota") },
                trailingContent = {
                    Switch(checked = false, onCheckedChange = {})
                },
                leadingContent = {
                    Icon(
                        Icons.Outlined.Visibility,
                        contentDescription = "Localized description",
                    )
                }
            )

        }
    }

}

fun items(function: () -> Unit) {

}
