package com.assignment.di

import com.assignment.location.LocationChangeComputer
import com.assignment.location.LocationPermissionHelper
import com.assignment.location.LocationProvider
import com.assignment.ui.restaurantsmap.viewmodel.RestaurantDetailsViewModel
import com.assignment.ui.restaurantsmap.viewmodel.RestaurantsViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val locationModule = module {

    factory {
        LocationPermissionHelper(context = androidContext())
    }

    factory { FusedLocationProviderClient(androidContext()) }

    factory { (thresholdDistance: Float) -> LocationChangeComputer(thresholdDistance = thresholdDistance) }

    single(createdAtStart = false) {
        LocationProvider(
            fusedLocationProviderClient = get()
        )
    }
}

val uiModule = module {

    viewModel {
        RestaurantsViewModel(
            foursquareDataSource = get(),
            locationProvider = get(),
            locationChangeComputer = get(parameters = { parametersOf(RestaurantsViewModel.THRESHOLD_LOCATION_CHANGE) })
        )
    }

    viewModel { RestaurantDetailsViewModel(foursquareDataSource = get()) }
}