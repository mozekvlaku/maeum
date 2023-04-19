package one.maeum.synapse.di

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import one.maeum.synapse.matter.service.MatterService
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.logging.HttpLoggingInterceptor
import one.maeum.synapse.matter.repository.MatterRepository
import one.maeum.synapse.ui.AppContainerViewModel
import one.maeum.synapse.ui.views.home.HomeViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val uiModule = module {
    viewModel { AppContainerViewModel(get()) }
    viewModel { HomeViewModel(get()) }
}

private val json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

val matterModule = module {
    single { createMatterService() }
    single { MatterRepository(get()) }
}

fun createMatterService() = createRetrofit().create(MatterService::class.java)
val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        Log.d("Retrofit", message)
    }
})



@OptIn(ExperimentalSerializationApi::class)
fun createRetrofit() = Retrofit.Builder().apply {
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
    client(okHttpClient)
    baseUrl("http://192.168.102.171:3000")
   // addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        addConverterFactory(GsonConverterFactory.create())
}.build()