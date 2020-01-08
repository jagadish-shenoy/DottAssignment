package com.assignment.ui.restaurantdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.assignment.ui.R
import com.assignment.foursquare.VenueDetails
import kotlinx.android.synthetic.main.activity_venue_details.*
import kotlinx.android.synthetic.main.layout_details_card.*

private const val EXTRA_VENUE_DETAILS = "venueDetails"

class RestaurantDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_venue_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val venueDetails: VenueDetails = requireArguments().getParcelable(EXTRA_VENUE_DETAILS)!!
        showVenueDetails(venueDetails)
    }

    fun showVenueDetails(venueDetails: VenueDetails) {
        venueDetails.apply {
            requireActivity().title = name
            if (photoUrl.isEmpty()) {
                venueImage.visibility = View.GONE
            } else {
                Glide.with(this@RestaurantDetailsFragment).load(photoUrl).into(venueImage)
            }
            venueDescriptionCard.setTitle(R.string.description)
            venueDescriptionCard.setDescription(description)

            venueContactCard.setTitle(R.string.contact)
            venueContactCard.setDescription(contactPhone)

            venueAddressCard.setTitle(R.string.address)
            venueAddressCard.setDescription(address)

            venueRatingCard.setTitle(R.string.rating)
            venueRatingCard.setDescription(rating)
        }
    }

    companion object {
        fun newInstance(venueDetails: VenueDetails) =  RestaurantDetailsFragment().apply {
            arguments = bundleOf(EXTRA_VENUE_DETAILS to venueDetails)
        }
    }
}
