package one.maeum.synapse.ui

import android.annotation.SuppressLint
import android.graphics.drawable.Icon
import android.os.Handler
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch
import one.maeum.synapse.R
import one.maeum.synapse.ai.MaeumSpeechRecognition
import one.maeum.synapse.matter.model.response.MatterState
import one.maeum.synapse.matter.repository.MatterRepository
import one.maeum.synapse.ui.navigation.BottomNavItem
import one.maeum.synapse.ui.theme.MaeumSynapseTheme
import one.maeum.synapse.ui.views.chat.ChatScreen
import one.maeum.synapse.ui.views.emotions.EmotionsScreen
import one.maeum.synapse.ui.views.home.HomeScreen
import one.maeum.synapse.ui.views.settings.SettingsScreen
import one.maeum.synapse.ui.views.visualcortex.VisualCortexScreen
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContainer(
    controller: NavHostController,
    viewModel: AppContainerViewModel = getViewModel { parametersOf() }
) {
    // HomeScreen()



        Surface(
            modifier = Modifier.fillMaxSize(),

            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter,

            ) {
                val navBackStackEntry by controller.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                var visibility = currentRoute == "home"
                    if (visibility) {
                        Image(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Your Vector Image",
                            modifier = Modifier.fillMaxSize(),

                            contentScale = ContentScale.FillWidth
                        )
                    }

                Log.d("Test", "Loading UI")

                MyUI(controller, viewModel)
            }
        }
    }






@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyUI(controller: NavHostController,
         viewModel: AppContainerViewModel) {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var skipPartiallyExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )
    val cstate = viewModel.matterState?.collectAsState()
    val state = viewModel.state.collectAsState()
    val context = LocalContext.current

    val recognition = MaeumSpeechRecognition(context)

    var SuperValue  by remember { mutableStateOf("...")}
    var Status by remember {mutableStateOf("Neposlouchá")}
    recognition.setCallbacks(
        {
            SuperValue += it

            Log.d("TAG", "Recognition append " + it)
        },
        {
            if (it != null) {
                SuperValue = it
                recognition.stop()


                    viewModel.sendToAI(it)

                Log.d("TAG", "Recognition done" + it)
            }
        },
        {
            scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                if (!bottomSheetState.isVisible) {
                    openBottomSheet = false
                }
            }

            Log.d("TAG", "Recognition stopped")
        },
        {
            Status = "👂🏻"

            Log.d("TAG", "Recognition listening")
        },
        {
            Status = "❌"

                SuperValue = "..."
                recognition.stop()
            Log.d("TAG", "Recognition stopped listening")
        }
    )


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { MyBottomBar(controller) },
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            MyTopBar(controller, viewModel)
        },
        floatingActionButton = { FloatingActionButton(
            onClick = { openBottomSheet = true
                recognition.start()},
        ) { Icon(Icons.Filled.Mic, "Localized Description") } },
        floatingActionButtonPosition = FabPosition.End
        /* scaffoldState = scaffoldState,*/

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NavHost(navController = controller, startDestination = DestinationHome) {
                composable(
                    route = DestinationHome
                ) {
                    HomeScreen(cstate = cstate as State<MatterState>, state = state)
                }
                composable(
                    route = DestinationChat
                ) {
                    ChatScreen(cstate = cstate as State<MatterState>)
                }
                composable(
                    route = DestinationEmotions
                ) {
                    EmotionsScreen()
                }
                composable(
                    route = DestinationVisualCortex
                ) {
                    VisualCortexScreen()
                }
                composable(
                    route = DestinationSettingsBase
                ) {
                    SettingsScreen(cstate = cstate as State<MatterState>, state = state)

                }
            }
            if (openBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        openBottomSheet = false
                        recognition.stop()
                                       },
                    sheetState = bottomSheetState,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Column(
                        modifier = Modifier
                            .padding(48.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {



                        Log.d("TAG", "Recognition start")
                        Text(
                            text=SuperValue,
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp
                        )


                    }

                }
            }
        }
    }
}


@ExperimentalMaterial3Api
@Composable
fun MyTopBar(controller: NavHostController, viewModel: AppContainerViewModel) {




    CenterAlignedTopAppBar(
        title = {
            Text(
                " ",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        ),
        actions = {
                val cls = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
                FilledTonalIconButton(onClick = { viewModel.emergency() }, colors = cls) {
                    Icon(Icons.Filled.Warning, "")
                }


        },
        navigationIcon = {

            IconButton(
                onClick = {
                    controller.navigate("settings") {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        controller.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }

            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Nastavení"
                )
            }
        }
    )
}

@Composable
fun MyBottomBar(controller: NavHostController) {
    // items list

    val bottomNavItems = listOf(
        BottomNavItem(
            name = "Domov",
            route = "home",
            icon = Icons.Outlined.Home
        ),
        BottomNavItem(
            name = "Zobrazení",
            route = "visual-cortex",
            icon = Icons.Outlined.Visibility
        ),
        BottomNavItem(
            name = "Chat",
            route = "chat",
            icon = Icons.Outlined.ChatBubbleOutline
        ),
        BottomNavItem(
            name = "Emoce",
            route = "emotions",
            icon = Icons.Outlined.Mood
        ),
    )

    androidx.compose.material3.NavigationBar(

    ) {
        bottomNavItems.forEach { item ->
            val navBackStackEntry by controller.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route


            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    controller.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        controller.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                label = {
                    Text(
                        text = item.name,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = "${item.name} Icon",
                    )
                }
            )
        }
    }

}


fun NavHostController.navigateCustom(route:String) {
    Log.d("TAG", "Navigate to " + route)
    try {
        navigate(route) {
            Log.d("TAG", "Navigated to " + route + " succesfully")
        }
    }
    catch ( e:Exception)
    {

        e.message?.let { Log.d("ERROR", it) }
    }
}


private fun String.replaceArg(argName: String, newString: String) =
    replace("{$argName}", newString)

private const val DestinationHome = "home"
private const val DestinationVisualCortex = "visual-cortex"
private const val DestinationEmotions = "emotions"
private const val DestinationChat = "chat"
private const val DestinationSettingsBase = "settings"

