package com.dott.foursquare

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.reflect.Type

/**
 * The Forusquare API JSON is heavily nested. JsonDeserializer to rescue.
 */
class VenueDetailsResultTypeAdapter : JsonDeserializer<VenueDetails> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): VenueDetails {
        val fullResponseJson = json.asJsonObject

        val venueDetailsResponseJson = fullResponseJson.getAsJsonObject("response")

        val venueDetailsJson = venueDetailsResponseJson.getAsJsonObject("venue")

        return VenueDetails(
            venueDetailsJson.getId(),
            venueDetailsJson.getName(),
            venueDetailsJson.getDescription(),
            venueDetailsJson.getPhotoUrl(),
            venueDetailsJson.getAddress(),
            venueDetailsJson.getPhone(),
            venueDetailsJson.getRating()
        )
    }

    private fun JsonObject.getId() = get("id").asString

    private fun JsonObject.getName() = get("name").asString

    private fun JsonObject.getDescription() = get("description")?.asString ?: "N/A"

    private fun JsonObject.getPhotoUrl():String {

        fun createPhotoUrl(prefix: String, suffix: String, width: String, height: String) =
            "$prefix${width}x$height$suffix"

        fun extractGroupOfTypeVenue(venueJson: JsonObject): JsonElement? {
            val groups = venueJson.getAsJsonObject("photos").getAsJsonArray("groups")?.asJsonArray
            return groups?.firstOrNull { "venue" == (it as JsonObject).get("type")?.asString }
        }

        val group = extractGroupOfTypeVenue(this)

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

    private fun JsonObject.getAddress():String {
        return getAsJsonObject("location")
            .getAsJsonArray("formattedAddress")?.asJsonArray?.joinToString(separator = "\n") {
            it.asString
        }
            ?: "N/A"
    }

    private fun JsonObject.getPhone():String {
        return getAsJsonObject("contact")?.get("formattedPhone")?.asString?:"N/A"
    }

    private fun JsonObject.getRating() = get("rating")?.asString ?: "N/A"
}