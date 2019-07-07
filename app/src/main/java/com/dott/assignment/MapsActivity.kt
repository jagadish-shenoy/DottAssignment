package com.dott.assignment

import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_maps.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


class MapsActivity : AppCompatActivity(),
    OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    private val fusedLocationProviderClient: FusedLocationProviderClient by inject { parametersOf(this) }

    private val locationPermissionHelper: LocationPermissionHelper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        (mapFragment as SupportMapFragment).getMapAsync(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (locationPermissionHelper.onRequestPermissionsResult(requestCode, grantResults)) {
            updateDeviceLocationOnMap()
        }
    }

    override fun onMapReady(_googleMap: GoogleMap) {
        googleMap = _googleMap
        locationPermissionHelper.apply {
            if(isPermissionGranted()) {
                updateDeviceLocationOnMap()
            } else {
                requestPermission(this@MapsActivity)
            }
        }
    }

    private fun updateDeviceLocationOnMap() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        result.locations.firstOrNull()?.apply {
                            googleMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        latitude,
                                        longitude
                                    ), 14.0f
                                )
                            )
                        }
                    }
                }, Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }
    }
}
