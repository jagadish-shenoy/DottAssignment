package com.assignment.ui.restaurantdetails

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.assignment.ui.R
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class RestaurantDetailsFragmentTest {

    private val venueDetails = com.assignment.foursquare.VenueDetails(
        "1",
        "Venue Name",
        "Venue Description",
        "https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png",
        "Venue Address",
        "+31123456789",
        "6.0"
    )


    @Test
    fun verifyAllRestaurantDetailsDisplayed() {

        launchFragmentInContainer(bundleOf("venueDetails" to venueDetails)) { RestaurantDetailsFragment() }

        onView(withText("Venue Description")).check(matches(isDisplayed()))
        onView(withText("Venue Address")).check(matches(isDisplayed()))
        onView(withText("+31123456789")).check(matches(isDisplayed()))
        onView(withText("6.0")).check(matches(isDisplayed()))

        onView(withId(R.id.venueImage)).check(matches(isDisplayed()))
    }
}