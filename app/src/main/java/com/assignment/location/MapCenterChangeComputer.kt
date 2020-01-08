package com.assignment.location

import android.location.Location
import com.google.android.gms.maps.model.LatLng

/**
 * [LocationSource] which reports the center of the google map when the map is panned / zoomed.
 */
class MapCenterChangeComputer {

    /**
     * Threshold distance to be panned before a new location is reported by this [LocationSource]
     */
    private val thresholdPanDistance = 250.0f

    private var lastCenter: LatLng? = null

    fun isCenterChangeSignificant(newCenter: LatLng): Boolean {
        val isChangeSignificant =
            lastCenter?.let { newCenter.distanceTo(it) > thresholdPanDistance } ?: false
        lastCenter = newCenter
        return isChangeSignificant
    }

    /**
     * Extension function to compute distance between 2 [LatLng]
     */
    private fun LatLng.distanceTo(otherLatLng:LatLng):Float {
        val result = FloatArray(3)
        Location.distanceBetween(latitude, longitude, otherLatLng.latitude, otherLatLng.longitude, result)
        return result[0]
    }
}