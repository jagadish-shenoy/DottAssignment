package com.assignment.ui.restaurantdetails

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.runner.RunWith
import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import com.assignment.ui.R
import org.junit.Rule
import org.junit.Test


@RunWith(AndroidJUnit4::class)
class RestaurantDetailsWithImageBehaviorTest {

    @get:Rule
    var mFragmentRule: ActivityTestRule<RestaurantDetailsFragment> = object : ActivityTestRule<RestaurantDetailsFragment>(RestaurantDetailsFragment::class.java) {

        override fun getActivityIntent(): Intent {
            val venueDetails = com.assignment.foursquare.VenueDetails(
                "1",
                "Venue Name",
                "Venue Description",
                "https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png",
                "Venue Address",
                "+31123456789",
                "6.0"
            )

            return Intent().apply {
                putExtra("venueDetails", venueDetails)
            }
        }
    }

    @Test
    fun verifyAllRestaurantDetailsDisplayed() {
        assertEquals("Venue Name", mFragmentRule.activity.title)
        Espresso.onView(ViewMatchers.withText("Venue Description")).check(matches(isDisplayed()))
        Espresso.onView(ViewMatchers.withText("Venue Address")).check(matches(isDisplayed()))
        Espresso.onView(ViewMatchers.withText("+31123456789")).check(matches(isDisplayed()))
        Espresso.onView(ViewMatchers.withText("6.0")).check(matches(isDisplayed()))
        
        Espresso.onView(withId(R.id.venueImage)).check(matches(isDisplayed()))
    }
}