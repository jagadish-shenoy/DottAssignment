package com.dott.assignment

import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

class LocationHelper(private val fusedLocationProviderClient: FusedLocationProviderClient) {

    private val _currentLocationLiveData: MutableLiveData<Location> = object:MutableLiveData<Location>() {
        override fun onActive() {
            super.onActive()
            updateCurrentLocation()
        }

        override fun onInactive() {
            super.onInactive()
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    val currentLocationLiveData:LiveData<Location>
    get() = _currentLocationLiveData

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.locations.firstOrNull()?.apply {
                _currentLocationLiveData.value = this
            }
        }
    }

    private fun updateCurrentLocation() {
        try {
            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback, Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }
    }
}