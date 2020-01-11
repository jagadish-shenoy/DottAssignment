package com.assignment.location

import android.location.Location
import com.google.android.gms.maps.model.LatLng

/**
 * Tracks the location changes and notifies if the change is significant based on
 * [thresholdDistance]
 */
class LocationChangeComputer(val thresholdDistance: Float) {

    private var lastCenter = LatLng(0.0, 0.0)

    fun isChangeSignificant(newCenter: LatLng): Boolean {
        val isChangeSignificant = newCenter.distanceTo(lastCenter) > thresholdDistance
        if (isChangeSignificant) {
            lastCenter = newCenter
        }
        return isChangeSignificant
    }

    /**
     * Extension function to compute distance between 2 [LatLng]
     */
    private fun LatLng.distanceTo(otherLatLng: LatLng): Float {
        val result = FloatArray(3)
        Location.distanceBetween(
            latitude,
            longitude,
            otherLatLng.latitude,
            otherLatLng.longitude,
            result
        )
        return result[0]
    }
}