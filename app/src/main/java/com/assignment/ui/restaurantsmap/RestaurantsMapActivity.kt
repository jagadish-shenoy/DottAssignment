package com.assignment.ui.restaurantsmap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.assignment.foursquare.FoursquareDataSource
import com.assignment.foursquare.FoursquareDataSource.VenueSearchResult.*
import com.assignment.ui.R
import com.assignment.ui.restaurantdetails.RestaurantDetailsActivity
import com.assignment.foursquare.Venue
import com.assignment.location.GpsLocationSource
import com.assignment.location.MapPanLocationSource
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_maps.*
import org.koin.android.viewmodel.ext.android.viewModel
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.getKoin

class RestaurantsMapActivity : AppCompatActivity(),
    OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

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

    override fun onMapReady(_googleMap: GoogleMap) {
        googleMap = _googleMap
        googleMap.moveCamera(CameraUpdateFactory.zoomBy(DEFAULT_MAP_ZOOM_LEVEL))
        prepareToHandleVenueDetails()
        prepareToReceiveRestaurants()
    }

    private fun prepareToReceiveRestaurants() {
        foursquareViewModel.addLocationSource(getKoin().get<GpsLocationSource>())
        foursquareViewModel.addLocationSource(MapPanLocationSource(googleMap))

        foursquareViewModel.restaurantsLiveData.observe(this,

            Observer<FoursquareDataSource.VenueSearchResult> {
                when(it) {
                    is Failure -> handleErrorFindingRestaurants()
                    is Success -> handleRestaurantsFound(it.venues)
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
            Observer<FoursquareDataSource.VenueDetailsResult> {
                when(it) {
                    is FoursquareDataSource.VenueDetailsResult.Success -> RestaurantDetailsActivity.start(it.venueDetails, this)
                    is FoursquareDataSource.VenueDetailsResult.Failure -> Snackbar.make(findViewById(android.R.id.content), R.string.error_finding_restaurant_details, Snackbar.LENGTH_LONG).show()
                }
            })
    }
}
