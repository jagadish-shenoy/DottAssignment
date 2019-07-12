package com.dott.assignment

import android.app.Activity
import android.app.Application
import com.google.android.gms.location.FusedLocationProviderClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

class MyApplication:Application() {

    private val appModule = module {

        factory {
            LocationPermissionHelper(androidContext())
        }

        factory { (activity: Activity) ->  FusedLocationProviderClient(activity) }

        factory { (activity: Activity) -> LocationHelper(get { parametersOf(activity)}) }

    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule)
        }
    }
}