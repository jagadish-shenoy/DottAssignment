package com.assignment.foursquare

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * The Forusquare API JSON is heavily nested. JsonDeserializer to rescue.
 */
class VenueSearchResultTypeAdapter : JsonDeserializer<Venues> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Venues {

        val fullResponseJson = json.asJsonObject

        val venueResponseJson = fullResponseJson.getAsJsonObject("response")

        val venuesJson = venueResponseJson.getAsJsonArray("venues")

        return Venues(venuesJson.map {
            val venueJson = it.asJsonObject
            val locationJson = venueJson.get("location").asJsonObject
            Venue(
                venueJson.get("id").asString,
                venueJson.get("name").asString,
                locationJson.get("lat").asDouble,
                locationJson.get("lng").asDouble
            )
        }.toList())
    }
}