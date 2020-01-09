package com.assignment.ui.di

import com.assignment.ui.restaurantsmap.viewmodel.CurrentLocationViewModel
import com.assignment.ui.restaurantsmap.viewmodel.RestaurantDetailsViewModel
import com.assignment.ui.restaurantsmap.viewmodel.RestaurantsViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {

    viewModel {
        RestaurantsViewModel(
            foursquareDataSource = get()
        )
    }

    viewModel { RestaurantDetailsViewModel(foursquareDataSource = get()) }

    viewModel {
        CurrentLocationViewModel(
            oneShotLocationProvider = get(),
            mapCenterChangeComputer = get()
        )
    }
}