package com.assignment.location.di

import com.assignment.location.LocationPermissionHelper
import com.assignment.location.MapCenterChangeComputer
import com.assignment.location.OneShotLocationProvider
import com.google.android.gms.location.FusedLocationProviderClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val locationModule = module {
    
    factory {
        LocationPermissionHelper(context = androidContext())
    }

    factory { FusedLocationProviderClient(androidContext()) }

    factory { MapCenterChangeComputer() }

    factory {
        OneShotLocationProvider(
            fusedLocationProviderClient = get()
        )
    }
}