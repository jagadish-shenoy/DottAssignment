package com.assignment.ui.restaurantdetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.assignment.ui.R
import com.assignment.foursquare.VenueDetails
import kotlinx.android.synthetic.main.activity_venue_details.*

private const val EXTRA_VENUE_DETAILS = "venueDetails"

class RestaurantDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venue_details)
        val venueDetails: VenueDetails = intent.extras!!.getParcelable(EXTRA_VENUE_DETAILS)!!

        venueDetails.apply {
            title = name
            if (photoUrl.isEmpty()) {
                venueImage.visibility = View.GONE
            } else {
                Glide.with(this@RestaurantDetailsActivity).load(photoUrl).into(venueImage)
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
        fun start(venueDetails: VenueDetails, context: Context) {
            val intent = Intent(context, RestaurantDetailsActivity::class.java)
            intent.putExtra(EXTRA_VENUE_DETAILS, venueDetails)
            context.startActivity(intent)
        }
    }
}
