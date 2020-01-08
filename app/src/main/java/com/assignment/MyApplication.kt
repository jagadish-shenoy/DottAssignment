package com.assignment

import android.app.Application
import com.assignment.foursquare.foursquareModule
import com.assignment.location.di.locationModule
import com.assignment.ui.di.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication:Application() {

    private val koinModules = listOf(uiModule, locationModule, foursquareModule)

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(koinModules)
        }
    }
}