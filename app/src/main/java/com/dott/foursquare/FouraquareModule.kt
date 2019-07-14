package com.dott.foursquare

import org.koin.dsl.module

val foursquareModule = module {

    factory {
        FoursquareDataSource()
    }
}