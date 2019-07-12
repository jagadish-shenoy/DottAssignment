package com.dott.foursquare

import org.koin.dsl.module

const val BASE_URL_FOURSQUARE = "https://api.foursquare.com/v2/"

const val KEY_CLIENT_ID = "client_id"

const val KEY_CLIENT_SECRET = "client_secret"

const val KEY_VERSION = "v"

const val VENUE_SEARCH_RADIUS = 1000 //1000m

const val VENUE_SEARCH_RESULT_LIMIT = 10


val foursquareModule = module {

    factory {
        RetrofitDataSource()
    }

}