package one.maeum.synapse.ui.views.visualcortex

import android.annotation.SuppressLint
import android.os.SystemClock
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material3.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import one.maeum.synapse.R
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit
@SuppressLint("StateFlowValueCalledInComposition")
@Preview
@Composable
fun VisualCortexScreen(
    viewModel: VisualCortexViewModel = getViewModel { parametersOf() },
) {
            val url = remember { mutableStateOf("http://${viewModel.ipAddr.value}:5001/synapse") }
            SystemClock.sleep(300)
            val uri = viewModel.ipAddr.value
            url.value = "http://${uri}:5001/synapse"

            var webView:WebView? by remember { mutableStateOf(null) }
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    Surface(modifier = Modifier.fillMaxSize()) {
                        AndroidView(
                            factory = { context ->
                                WebView(context).apply {
                                    webViewClient = WebViewClient()
                                    webChromeClient = WebChromeClient()
                                    loadUrl(url.value)
                                }.also { webView = it }
                            },
                            update = { view ->
                                view.loadUrl(url.value)
                            }
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.matterload),

                        contentDescription = "Načítání",
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                }
            }



}
