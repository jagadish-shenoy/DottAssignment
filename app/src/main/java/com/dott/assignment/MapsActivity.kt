package com.dott.assignment

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.dott.foursquare.Venue
import com.dott.foursquare.VenueDetails
import com.dott.location.GpsLocationSource
import com.dott.location.LocationPermissionHelper
import com.dott.location.MapPanLocationSource
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_maps.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import com.google.android.gms.maps.model.MarkerOptions
import org.koin.android.ext.android.getKoin

class MapsActivity : AppCompatActivity(),
    OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    private val locationPermissionHelper: LocationPermissionHelper by inject()

    private val foursquareViewModel:FoursquareViewModel by viewModel()

    private var isCameraAlignmentNeeded = true

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
        googleMap.moveCamera(CameraUpdateFactory.zoomBy(14.0f))

        googleMap.setOnInfoWindowClickListener {
            foursquareViewModel.fetchVenueDetails((it.tag as Venue).id)
        }
        locationPermissionHelper.apply {
            if(isPermissionGranted()) {
                observerVenueDetials()
                updateDeviceLocationOnMap()
            } else {
                requestPermission(this@MapsActivity)
            }
        }
    }

    private fun updateDeviceLocationOnMap() {
        foursquareViewModel.addLocationSource(getKoin().get<GpsLocationSource>())
        foursquareViewModel.addLocationSource(MapPanLocationSource(googleMap))

        foursquareViewModel.venuesLiveData.observe(this,
            Observer<List<Venue>> {
                if(it.isNotEmpty()) {
                    it.forEach { venue ->
                        val latLng = LatLng(venue.latitude, venue.longitude)
                        val marker = googleMap.addMarker(
                            MarkerOptions().position(latLng)
                                .title(venue.name)
                        )
                        marker.tag = venue
                    }

                    if(isCameraAlignmentNeeded) {
                        googleMap.moveCamera(
                            CameraUpdateFactory.newLatLng(
                                it.first().let { firstVenue ->
                                    LatLng(firstVenue.latitude, firstVenue.longitude)
                                }
                            )
                        )
                        isCameraAlignmentNeeded = false
                    }
                }
            })
    }

    private fun observerVenueDetials() {
        foursquareViewModel.venueDetailsLiveData.observe(this,
            Observer<VenueDetails> {
                VenueDetailsActivity.start(it, this)
            })
    }
}
