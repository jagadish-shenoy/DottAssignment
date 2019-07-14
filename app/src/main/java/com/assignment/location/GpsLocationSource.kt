package com.assignment.location

import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.location.LocationCallback as GoogleLocationCallback

/**
 * [LocationSource] to fetch device location using sensor fusion.
 */
class GpsLocationSource(private val fusedLocationProviderClient: FusedLocationProviderClient) : LocationSource() {

    override fun onActive() {
        requestCurrentLocation()
    }

    override fun onInActive() {
        fusedLocationProviderClient.removeLocationUpdates(callback)
    }

    private val callback = object : GoogleLocationCallback() {
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