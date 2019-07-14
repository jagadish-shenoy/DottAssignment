package com.dott.assignment

import android.app.Application
import com.dott.assignment.restaurantsmap.FoursquareViewModel
import com.dott.foursquare.foursquareModule
import com.dott.location.GpsLocationSource
import com.dott.location.LocationPermissionHelper
import com.google.android.gms.location.FusedLocationProviderClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApplication:Application() {

    private val appModule = module {

        factory {
            LocationPermissionHelper(androidContext())
        }

        factory { FusedLocationProviderClient(androidContext()) }

        factory { GpsLocationSource(get()) }

        viewModel { FoursquareViewModel(get()) }

    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(listOf(appModule, foursquareModule))
        }
    }
}