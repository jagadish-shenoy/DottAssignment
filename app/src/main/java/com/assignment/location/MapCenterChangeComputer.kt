package com.assignment.location

import android.location.Location
import com.google.android.gms.maps.model.LatLng

/**
 * Every time the map is panned, this class helps check if user panned more than 250m
 */
class MapCenterChangeComputer {

    /**
     * Threshold pan distance
     */
    private val thresholdPanDistance = 250.0f

    private var lastCenter: LatLng? = null

    fun isCenterChangeSignificant(newCenter: LatLng): Boolean {
        return if (lastCenter == null) {
            lastCenter = newCenter
            false
        } else if (newCenter.distanceTo(lastCenter!!) > thresholdPanDistance) {
            lastCenter = newCenter
            true
        } else {
            false
        }
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