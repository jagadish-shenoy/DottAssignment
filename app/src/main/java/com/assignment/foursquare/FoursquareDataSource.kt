package com.assignment.foursquare

import java.io.IOException

class FoursquareDataSource(private val venueService: VenueService) {

    suspend fun searchRestaurants(lat: Double, long: Double, radius:Int, limit: Int): VenueSearchResult {
        return try {
            val response = venueService.searchVenues("$lat,$long", FOURSQUARE_CATEGORY_ID, radius, limit)
            if (!response.isSuccessful || response.body() == null) {
                VenueSearchResult.Failure
            } else {
                VenueSearchResult.Success((response.body() as Venues).list)
            }
        } catch (e:IOException) {
            VenueSearchResult.Failure
        }
    }

    suspend fun fetchRestaurantDetails(venueId: String): VenueDetailsResult {
        return try {
            val response = venueService.getVenueDetails(venueId)
            return if (!response.isSuccessful || response.body() == null) {
                VenueDetailsResult.Failure
            } else {
                VenueDetailsResult.Success(response.body() as VenueDetails)
            }
        } catch (e:IOException) {
            VenueDetailsResult.Failure
        }
    }
}