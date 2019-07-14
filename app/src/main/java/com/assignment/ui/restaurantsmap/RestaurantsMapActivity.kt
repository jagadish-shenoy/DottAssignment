package com.assignment.ui.restaurantsmap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.assignment.ui.R
import com.assignment.ui.restaurantdetails.VenueDetailsActivity
import com.assignment.foursquare.Venue
import com.assignment.foursquare.VenueDetails
import com.assignment.location.GpsLocationSource
import com.assignment.location.LocationPermissionHelper
import com.assignment.location.MapPanLocationSource
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

class RestaurantsMapActivity : AppCompatActivity(),
    OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    private val locationPermissionHelper: LocationPermissionHelper by inject()

    private val foursquareViewModel: FoursquareViewModel by viewModel()

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
            foursquareViewModel.fetchRestaurantDetails((it.tag as Venue).id)
        }
        locationPermissionHelper.apply {
            if (isPermissionGranted()) {
                observerVenueDetails()
                updateDeviceLocationOnMap()
            } else {
                requestPermission(this@RestaurantsMapActivity)
            }
        }
    }

    private fun updateDeviceLocationOnMap() {
        foursquareViewModel.addLocationSource(getKoin().get<GpsLocationSource>())
        foursquareViewModel.addLocationSource(MapPanLocationSource(googleMap))

        foursquareViewModel.restaurantsLiveData.observe(this,
            Observer<List<Venue>> {
                if (it.isNotEmpty()) {
                    it.forEach { venue ->
                        val latLng = LatLng(venue.latitude, venue.longitude)
                        val marker = googleMap.addMarker(
                            MarkerOptions().position(latLng)
                                .title(venue.name)
                        )
                        marker.tag = venue
                    }

                    if (isCameraAlignmentNeeded) {
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

    private fun observerVenueDetails() {
        foursquareViewModel.restaurantDetailsLiveData.observe(this,
            Observer<VenueDetails> {
                VenueDetailsActivity.start(it, this)
            })
    }
}
