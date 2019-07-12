package com.dott.foursquare
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
data class VenueDetails(val id:String,
                        val name: String,
                        val description: String,
                        val photoUrl: String,
                        val address: String,
                        val contactPhone: String,
                        val rating: String)

/**
 * Wrapper for API result carries status + data for Venue search
 */
sealed class VenueSearchResult {

    class Success(val venues: List<Venue>): VenueSearchResult()

    object Failure: VenueSearchResult()
}

sealed class VenueDetailsResult {

    class Success(val venueDetails: VenueDetails): VenueDetailsResult()

    object Failure: VenueDetailsResult()
}