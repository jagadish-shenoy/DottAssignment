package com.assignment.foursquare

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

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