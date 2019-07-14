package com.dott.foursquare

/**
 * Koin Module for com.dott.foursquare package.
 */
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val foursquareModule = module {

    single {
        createVenueService(androidContext())
    }

    factory {
        FoursquareDataSource(get())
    }
}