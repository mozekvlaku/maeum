package one.maeum.synapse.di

import android.util.Log
import androidx.compose.runtime.collectAsState
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import one.maeum.synapse.matter.service.MatterService
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.logging.HttpLoggingInterceptor
import one.maeum.synapse.datastore.SynapseDataStore
import one.maeum.synapse.matter.repository.MatterRepository
import one.maeum.synapse.matter.repository.MatterSocketRepository
import one.maeum.synapse.ui.AppContainerViewModel
import one.maeum.synapse.ui.views.emotions.EmotionsViewModel
import one.maeum.synapse.ui.views.home.HomeViewModel
import one.maeum.synapse.ui.views.settings.SettingsViewModel
import one.maeum.synapse.ui.views.visualcortex.VisualCortexViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.compose.inject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val uiModule = module {
    viewModel { AppContainerViewModel(get(), get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { VisualCortexViewModel(get()) }
    viewModel { EmotionsViewModel(get()) }
    viewModel { SettingsViewModel(get(), get()) }
//    single { androidApplication() }
}

private val json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

val matterModule = module {
    single { createMatterService() }
    single { MatterRepository(get()) }
    single { SynapseDataStore }
    single { MatterSocketRepository() }
}

fun createMatterService() = runBlocking {
    withContext(Dispatchers.IO) {
        createRetrofit().create(MatterService::class.java)
    }
}


val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        Log.d("Retrofit", message)
    }
})



@OptIn(ExperimentalSerializationApi::class)
suspend fun createRetrofit() = Retrofit.Builder().apply {
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY


    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
    client(okHttpClient)
    baseUrl("http://${SynapseDataStore.ipAddressFlow.first()}:${SynapseDataStore.portRestFlow.first()}")
   // addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        addConverterFactory(GsonConverterFactory.create())
}.build()