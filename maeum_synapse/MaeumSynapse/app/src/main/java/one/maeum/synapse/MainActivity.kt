package one.maeum.synapse

import android.content.res.Configuration
import android.os.Bundle
import android.view.Surface
import android.view.Window
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import one.maeum.synapse.ui.AppContainer
import one.maeum.synapse.ui.navigateCustom
import one.maeum.synapse.ui.navigation.BottomNavItem
import one.maeum.synapse.ui.theme.MaeumSynapseTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            var darkMode = false
            if(resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)
                darkMode = true

            MaeumSynapseTheme(useDarkTheme = darkMode) {
                val window: Window = this.window
                window.navigationBarColor = MaterialTheme.colorScheme.secondaryContainer.toArgb()
                //window.statusBarColor = MaterialTheme.colorScheme.background.toArgb()¨

                AppContainer(controller = rememberNavController())
            }
        }
    }
}
