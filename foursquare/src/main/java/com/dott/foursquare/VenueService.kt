package com.dott.foursquare

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface VenueService {

    @GET("venues/search")
    fun searchVenues(@Query("ll") latLong: String,
                     @Query("categoryId") categoryId:String,
                     @Query("radius") radius: Int,
                     @Query("limit") limit: Int): Call<Venues>

    @GET("venues/{venue_id}")
    fun getVenueDetails(@Path("venue_id") venueId: String): Call<VenueDetails>
}