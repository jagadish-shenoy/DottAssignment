package com.assignment.location

import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

/**
 * [LocationSource] which reports the center of the google map when the map is panned / zoomed.
 */
class MapPanLocationSource(private val googleMap:GoogleMap):LocationSource() {

    /**
     * Threshold distance to be panned before a new location is reported by this [LocationSource]
     */
    private val thresholdPanDistance = 250.0f

    private val cameraIdleListener = object:GoogleMap.OnCameraIdleListener {

        private var lastCenter: LatLng? = null

        override fun onCameraIdle() {
            val currentCenter = googleMap.cameraPosition.target
            lastCenter?.let { lastCenter ->
                if (currentCenter.distanceTo(lastCenter) > thresholdPanDistance) {
                    locationCallback?.onNewLocation(currentCenter)
                }
            }
            lastCenter = currentCenter
        }
    }

    override fun onActive() {
        googleMap.setOnCameraIdleListener(cameraIdleListener)
    }

    override fun onInActive() {
        googleMap.setOnCameraIdleListener(null)
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