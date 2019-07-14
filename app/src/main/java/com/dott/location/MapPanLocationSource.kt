package com.dott.location

import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

class MapPanLocationSource(private val googleMap:GoogleMap):LocationSource() {

    private val thresholdPanDistance = 250.0f

    private val cameraIdleListener = object:GoogleMap.OnCameraIdleListener {

        private var lastCenter: LatLng? = null

        override fun onCameraIdle() {
            val currentCenter = googleMap.cameraPosition.target
            if(lastCenter != null && currentCenter.distanceTo(lastCenter!!) > thresholdPanDistance) {
                locationCallback?.onNewLocation(currentCenter)
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

    private fun LatLng.distanceTo(otherLatLng:LatLng):Float {
        val result = FloatArray(3)
        Location.distanceBetween(latitude, longitude, otherLatLng.latitude, otherLatLng.longitude, result)
        return result[0]
    }
}