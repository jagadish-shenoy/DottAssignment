package com.assignment.foursquare

class FoursquareDataSource(private val venueService: VenueService) {

    suspend fun searchRestaurants(lat: Double, long: Double, radius:Int, limit: Int): VenueSearchResult {
            val response = venueService.searchVenues("$lat,$long", FOURSQUARE_CATEGORY_ID, radius, limit)
            return if (!response.isSuccessful || response.body() == null) {
                VenueSearchResult.Failure
            } else {
                VenueSearchResult.Success((response.body() as Venues).list)
            }
    }

    suspend fun fetchRestaurantDetails(venueId: String): VenueDetailsResult {
        val response = venueService.getVenueDetails(venueId)
        return if (!response.isSuccessful || response.body() == null) {
            VenueDetailsResult.Failure
        } else {
            VenueDetailsResult.Success(response.body() as VenueDetails)
        }
    }
}