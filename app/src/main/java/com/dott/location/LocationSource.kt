package com.dott.location

import com.google.android.gms.maps.model.LatLng

/**
 * Base class for any implementation that is capable of proving the location of interest to user.
 *
 * The implementation can start fetching the location on [onActive] callback
 * and stop fetching location on [onInActive]
 *
 * [onActive] is invoked when there is at least one [LocationCallback] is registered
 * [onInActive] is invoked when there are no [LocationCallback] left.
 */
abstract class LocationSource {

    interface LocationCallback {
        fun onNewLocation(latLng:LatLng)
    }

    protected abstract fun onActive()

    protected abstract fun onInActive()

    var locationCallback:LocationCallback? = null
    set(value) {
        val lastCallback = locationCallback
        field = value
        if(value == null) {
            onInActive()
        } else if(lastCallback == null) {
            onActive()
        }
    }
}