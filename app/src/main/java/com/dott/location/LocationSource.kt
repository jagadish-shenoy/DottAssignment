package com.dott.location

import com.google.android.gms.maps.model.LatLng

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