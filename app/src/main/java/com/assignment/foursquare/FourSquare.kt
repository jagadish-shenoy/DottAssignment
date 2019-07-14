package com.assignment.foursquare

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

const val BASE_URL_FOURSQUARE = "https://api.foursquare.com/v2/"

const val KEY_CLIENT_ID = "client_id"

const val KEY_CLIENT_SECRET = "client_secret"

const val KEY_VERSION = "v"

const val FOURSQUARE_CATEGORY_ID = "4d4b7105d754a06374d81259"

/**
 * Represents a Venue for the Search result
 */

data class Venue(val id: String, val name: String, val latitude:Double, val longitude:Double)

/**
 * Wrapper class for List<Venue> - to simplify JSON parsing
 */
data class Venues(val list: List<Venue>)

/**
 * Wrapper class for Venue with more details.
 */
@Parcelize
data class VenueDetails(val id:String,
                        val name: String,
                        val description: String,
                        val photoUrl: String,
                        val address: String,
                        val contactPhone: String,
                        val rating: String) : Parcelable

/**
 * Wrapper for venue Search API result
 */
sealed class VenueSearchResult {

    class Success(val venues: List<Venue>): VenueSearchResult()

    object Failure: VenueSearchResult()
}

/**
 * Wrapper for venue details API result
 */
sealed class VenueDetailsResult {

    class Success(val venueDetails: VenueDetails): VenueDetailsResult()

    object Failure: VenueDetailsResult()
}