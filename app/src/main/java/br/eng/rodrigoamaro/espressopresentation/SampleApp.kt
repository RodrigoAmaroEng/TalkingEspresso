package br.eng.rodrigoamaro.espressopresentation

import android.app.Application
import android.content.Context
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.android.startKoin
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext

class SampleApp : Application() {
    private val module = module {
        single<CoroutineDispatcher>(override = true) { Dispatchers.Default }
        single<Api>(override = true) { RealApi() }
    }

    override fun onCreate() {
        super.onCreate()
        StandAloneContext.stopKoin()
        startKoin(this, listOf(module))
    }
}

fun Context.app() = this as SampleApp
