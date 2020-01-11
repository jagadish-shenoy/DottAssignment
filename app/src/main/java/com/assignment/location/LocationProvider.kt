package com.assignment.location

import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng

/**
 * Single point for fetching the current location.
 * Initializes itself with the current location on start.
 *
 * Location can be modified using [setCurrentLocation] api
 */
class LocationProvider(private val fusedLocationProviderClient: FusedLocationProviderClient) {

    val locationLiveData: LiveData<LatLng>
        get() = _locationLiveData

    /**
     * Set a new location as current location, obtained from sources like Google map
     */
    fun setCurrentLocation(newLocation:LatLng) {
        _locationLiveData.postValue(newLocation)
    }

    private val _locationLiveData = object : MutableLiveData<LatLng>() {
        override fun onActive() {
            requestCurrentLocation()
        }

        override fun onInactive() {
            stopLocationUpdate()
        }
    }

    private val callback = object : com.google.android.gms.location.LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.locations.firstOrNull()?.apply {
                _locationLiveData.postValue(LatLng(latitude, longitude))
                stopLocationUpdate()
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

    private fun stopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(callback)
    }
}