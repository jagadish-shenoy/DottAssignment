package com.dott.foursquare

import com.dott.foursquare.VenueDetails
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
class VenueDetailsResultTypeAdapter : JsonDeserializer<VenueDetails> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): VenueDetails {
        val fullResponseJson = json.asJsonObject

        val venueDetailsResponseJson = fullResponseJson.getAsJsonObject("response")

        val venueDetailsJson = venueDetailsResponseJson.getAsJsonObject("venue")

        return VenueDetails(
                venueDetailsJson.get("id").asString,
                venueDetailsJson.get("name").asString,
                venueDetailsJson.get("description")?.asString ?: "N/A",
                extractPhoto(venueDetailsJson),
                extractAddressFromLocation(venueDetailsJson),
                extractPhoneContact(venueDetailsJson),
                venueDetailsJson.get("rating")?.asString ?: "N/A"
        )
    }

    private fun extractAddressFromLocation(venueJson: JsonObject) =
            venueJson.getAsJsonObject("location")
                    .getAsJsonArray("formattedAddress")?.asJsonArray?.joinToString(separator = "\n") {
                it.asString
            }
                    ?: "N/A"

    private fun extractPhoneContact(venueJson: JsonObject) =
            venueJson.getAsJsonObject("contact")?.get("formattedPhone")?.asString?:"N/A"

    private fun extractPhoto(venueJson: JsonObject): String {
        val group = extractGroupOfTypeVenue(venueJson)

        return group?.asJsonObject?.let {
            val photoItem = it.getAsJsonArray("items")?.firstOrNull()?.asJsonObject
            photoItem?.let {
                createPhotoUrl(photoItem.get("prefix").asString,
                        photoItem.get("suffix").asString,
                        photoItem.get("width").asString,
                        photoItem.get("height").asString)
            }
        } ?: ""
    }

    private fun extractGroupOfTypeVenue(venueJson: JsonObject): JsonElement? {
        val groups = venueJson.getAsJsonObject("photos").getAsJsonArray("groups")?.asJsonArray
        return groups?.firstOrNull { "venue" == (it as JsonObject).get("type")?.asString }
    }

    private fun createPhotoUrl(prefix: String, suffix: String, width: String, height: String) =
            "$prefix${width}x$height$suffix"
}