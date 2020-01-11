package com.assignment

import android.app.Application
import com.assignment.di.locationModule
import com.assignment.di.uiModule
import com.assignment.foursquare.foursquareModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(listOf(uiModule, locationModule, foursquareModule))
        }
    }
}