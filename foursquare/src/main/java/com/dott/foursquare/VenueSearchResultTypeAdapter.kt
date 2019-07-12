package com.dott.foursquare

import com.dott.foursquare.Venue
import com.dott.foursquare.Venues
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

/**
 * The Forusquare API JSON is heavily nested. JsonDeserializer to rescue.
 *
 * TODO Convert the attribute reading from JsonObject to extension methods for readability
 */
class VenueSearchResultTypeAdapter : JsonDeserializer<Venues> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Venues {

        val fullResponseJson = json.asJsonObject

        val venueResponseJson = fullResponseJson.getAsJsonObject("response")

        val venuesJson = venueResponseJson.getAsJsonArray("venues")

        return Venues(venuesJson.map {
            val venueJson = it.asJsonObject

            Venue(venueJson.get("id").asString,
                    venueJson.get("name").asString,
                    extractAddressFromLocation(venueJson.get("location").asJsonObject))
        }.toList())
    }

    private fun extractAddressFromLocation(venueJson: JsonObject?) =
            venueJson?.let {
                val formattedAddress = it.get("formattedAddress").asJsonArray
                formattedAddress?.joinToString(separator = "\n") {
                    it.asString
                } ?: "N/A"
            } ?: "N/A"
}