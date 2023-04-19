package one.maeum.synapse

import android.app.Application
import one.maeum.synapse.di.matterModule
import one.maeum.synapse.di.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.*
import org.koin.core.context.GlobalContext.startKoin

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(applicationContext)
            modules(listOf(uiModule,matterModule))
        }
    }

}