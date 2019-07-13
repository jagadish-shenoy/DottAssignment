package com.dott.assignment

import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.dott.foursquare.Venue
import com.dott.location.LocationPermissionHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_maps.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(),
    OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    private val locationPermissionHelper: LocationPermissionHelper by inject()

    private val foursquareViewModel:FoursquareViewModel by viewModel()

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


        googleMap.setOnCameraIdleListener(object:GoogleMap.OnCameraIdleListener {

            private var lastCenter:LatLng? = null

            override fun onCameraIdle() {
                val currentCenter = googleMap.cameraPosition.target
                if(lastCenter != null && currentCenter.distanceTo(lastCenter!!) > 200.0f) {
                    Log.i("PAN", "Map panned more than 200 meters")
                }
                lastCenter = currentCenter
            }
        })
    }

    fun LatLng.distanceTo(otherLatLng:LatLng):Float {
        val result = FloatArray(3)
        Location.distanceBetween(latitude, longitude, otherLatLng.latitude, otherLatLng.longitude, result)
        return result[0]
    }


    private fun updateDeviceLocationOnMap() {
        foursquareViewModel.venuesLiveData.observe(this,
            Observer<List<Venue>> {
                it.forEach { venue ->
                    val latLng = LatLng(venue.latitude, venue.longitude)
                    googleMap.addMarker(
                        MarkerOptions().position(latLng)
                            .title(venue.name)
                    )

                    googleMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            latLng, 14.0f
                        )
                    )
                }
            })
    }
}
