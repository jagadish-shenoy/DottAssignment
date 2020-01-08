package com.assignment.location

import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng

class LocationSource(private val fusedLocationProviderClient: FusedLocationProviderClient) {

    interface LocationCallback {
        fun onNewLocation(latLng:LatLng)
    }

    fun start(callback: LocationCallback) {
        this.locationCallback = callback
        requestCurrentLocation()
    }

    fun stop() {
        fusedLocationProviderClient.removeLocationUpdates(callback)
        this.locationCallback = null
    }

    private var locationCallback: LocationCallback? = null

    private val callback = object : com.google.android.gms.location.LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.locations.firstOrNull()?.apply {
                locationCallback?.onNewLocation(LatLng(latitude, longitude))
            }
        }
    }

    private fun requestCurrentLocation() {
        try {
            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                callback, Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }
    }
}