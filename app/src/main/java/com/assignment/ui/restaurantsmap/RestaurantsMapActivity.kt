package com.assignment.ui.restaurantsmap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.assignment.ui.R
import com.assignment.ui.restaurantdetails.VenueDetailsActivity
import com.assignment.foursquare.Venue
import com.assignment.foursquare.VenueDetailsResult
import com.assignment.foursquare.VenueSearchResult
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
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.getKoin

class RestaurantsMapActivity : AppCompatActivity(),
    OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    private val locationPermissionHelper: LocationPermissionHelper by inject()

    private val foursquareViewModel: FoursquareViewModel by viewModel()

    private var isCameraAlignmentNeeded = true

    companion object {
        const val DEFAULT_MAP_ZOOM_LEVEL = 14.0f
    }

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
            prepareToReceiveRestaurants()
        } else if(locationPermissionHelper.isPermissionDenied(this)) {
            Snackbar.make(findViewById(android.R.id.content),
                R.string.location_permission_denied,
                Snackbar.LENGTH_INDEFINITE).show()
        } else {
            Snackbar.make(findViewById(android.R.id.content),
                R.string.restart_app_grant_permission,
                Snackbar.LENGTH_INDEFINITE).show()
        }
    }

    override fun onMapReady(_googleMap: GoogleMap) {
        googleMap = _googleMap
        googleMap.moveCamera(CameraUpdateFactory.zoomBy(DEFAULT_MAP_ZOOM_LEVEL))
        locationPermissionHelper.apply {
            if (isPermissionGranted()) {
                prepareToHandleVenueDetails()
                prepareToReceiveRestaurants()
            } else {
                requestPermission(this@RestaurantsMapActivity)
            }
        }
    }

    private fun prepareToReceiveRestaurants() {
        foursquareViewModel.addLocationSource(getKoin().get<GpsLocationSource>())
        foursquareViewModel.addLocationSource(MapPanLocationSource(googleMap))

        foursquareViewModel.restaurantsLiveData.observe(this,

            Observer<VenueSearchResult> {
                when(it) {
                    is VenueSearchResult.Failure -> handleErrorFindingRestaurants()
                    is VenueSearchResult.Success -> handleRestaurantsFound(it.venues)
                }
            })
    }

    private fun handleErrorFindingRestaurants() {
        Snackbar.make(findViewById(android.R.id.content), R.string.error_finding_restaurants, Snackbar.LENGTH_LONG).show()
    }

    private fun handleRestaurantsFound(venues:List<Venue>) {
        if (venues.isNotEmpty()) {
            venues.forEach { venue ->
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
                        venues.first().let { firstVenue ->
                            LatLng(firstVenue.latitude, firstVenue.longitude)
                        }
                    )
                )
                isCameraAlignmentNeeded = false
            }
        }
    }

    private fun prepareToHandleVenueDetails() {
        googleMap.setOnInfoWindowClickListener {
            foursquareViewModel.fetchRestaurantDetails((it.tag as Venue).id)
        }

        foursquareViewModel.restaurantDetailsLiveData.observe(this,
            Observer<VenueDetailsResult> {
                when(it) {
                    is VenueDetailsResult.Success -> VenueDetailsActivity.start(it.venueDetails, this)
                    is VenueDetailsResult.Failure -> Snackbar.make(findViewById(android.R.id.content), R.string.error_finding_restaurant_details, Snackbar.LENGTH_LONG).show()
                }
            })
    }
}
