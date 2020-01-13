package com.assignment.ui.restaurantsmap

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.assignment.foursquare.FoursquareDataSource.VenueDetailsResult
import com.assignment.foursquare.FoursquareDataSource.VenueSearchResult
import com.assignment.foursquare.Venue
import com.assignment.location.LocationProvider
import com.assignment.ui.R
import com.assignment.ui.restaurantdetails.EXTRA_VENUE_DETAILS
import com.assignment.ui.restaurantsmap.viewmodel.RestaurantDetailsViewModel
import com.assignment.ui.restaurantsmap.viewmodel.RestaurantsViewModel
import com.google.android.gms.maps.CameraUpdateFactory.newCameraPosition
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel


class RestaurantsMapFragment : Fragment(), OnMapReadyCallback {

    companion object {

        private const val SAVED_STATE_MAP_ALIGNMENT_NEEDED = "mapAlignmentNeeded"

        const val DEFAULT_MAP_ZOOM_LEVEL = 14.0f
    }

    private val restaurantsViewModel: RestaurantsViewModel by viewModel()

    private val restaurantDetailsViewModel: RestaurantDetailsViewModel by viewModel()

    private val locationProvider: LocationProvider by inject()

    private lateinit var googleMap: GoogleMap

    private val navController
        get() = requireView().findNavController()

    /**
     * Tracks if camera needs to be aligned to the search result. Unless this state is saved
     * in savedInstanceState, the map would zoom again on rotation.
     */
    private var mapAlignmentNeeded = true

    /**
     * Tracks if the camera idle listener has been set to notify pan events.
     */
    private var setCameraIdleListener = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        restoreState(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = layoutInflater.inflate(R.layout.fragment_restaurants_maps, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment)
            .getMapAsync(this)
    }

    override fun onMapReady(_googleMap: GoogleMap) {
        googleMap = _googleMap
        prepareToHandleVenueDetails()
        prepareToReceiveRestaurants()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVED_STATE_MAP_ALIGNMENT_NEEDED, mapAlignmentNeeded)
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        mapAlignmentNeeded = savedInstanceState?.getBoolean(SAVED_STATE_MAP_ALIGNMENT_NEEDED)?:true
    }

    private fun prepareToReceiveRestaurants() {
        restaurantsViewModel.restaurantsLiveData.observe(this,

            Observer<VenueSearchResult> {
                when (it) {
                    is VenueSearchResult.Failure -> showError(R.string.error_finding_restaurants)
                    is VenueSearchResult.Success -> handleRestaurantsFound(it)
                }
            })
    }

    private fun prepareToHandleVenueDetails() {
        googleMap.setOnInfoWindowClickListener {
            restaurantDetailsViewModel.fetchRestaurantDetails((it.tag as Venue).id)
        }

        restaurantDetailsViewModel.restaurantDetailsLiveData.observe(this,
            Observer<VenueDetailsResult> {
                when (it) {
                    is VenueDetailsResult.Success -> navController.navigate(
                        R.id.action_restaurantsMapFragment_to_restaurantDetailsFragment,
                        bundleOf(EXTRA_VENUE_DETAILS to it.venueDetails)
                    )

                    is VenueDetailsResult.Failure -> showError(R.string.error_finding_restaurant_details)
                }
            })
    }

    private fun handleRestaurantsFound(venueSearchResult: VenueSearchResult.Success) {
        venueSearchResult.venues.forEach { venue ->
            val latLng = LatLng(venue.latitude, venue.longitude)
            val marker = googleMap.addMarker(MarkerOptions().position(latLng).title(venue.name))
            marker.tag = venue
            Log.i("log", venue.toString())
        }

        if(mapAlignmentNeeded) {
            alignMapToLatLong(LatLng(venueSearchResult.lat, venueSearchResult.long))
        }

        if(setCameraIdleListener) {
           setCameraIdleListener()
        }
    }

    private fun alignMapToLatLong(latLng: LatLng) {
        val cameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(DEFAULT_MAP_ZOOM_LEVEL)
            .build()
        googleMap.animateCamera(newCameraPosition(cameraPosition))
        mapAlignmentNeeded = false
    }

    private fun setCameraIdleListener() {
        googleMap.setOnCameraIdleListener {
            locationProvider.setCurrentLocation(googleMap.cameraPosition.target)
        }
        setCameraIdleListener = false
    }

    private fun showError(@StringRes error: Int) {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            error,
            Snackbar.LENGTH_LONG
        ).show()
    }
}
