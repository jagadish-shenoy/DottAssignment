package com.assignment.location.ui

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.GrantPermissionRule
import com.assignment.ui.R
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule


class LocationPermissionRequestFragmentTest {

    @get:Rule
    var mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    var mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    @Mock
    private lateinit var navController: NavController

    @Test
    fun verifyLocationPermissionScreenElements() {
        startFragmentWithMockNavController()

        Espresso.onView(withId(R.id.location_permission_rationale))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.location_permission_request)))

        Espresso.onView(withId(R.id.ready_to_grant_permission))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.ready_to_grant_permission)))
    }

    @Test
    fun shouldNavigateToRestaurantsMapScreenOnPermissionGrant() {
        startFragmentWithMockNavController()

        Espresso.onView(withId(R.id.ready_to_grant_permission))
            .check(matches(isDisplayed()))
            .perform(click())

        verify(navController).navigate(R.id.action_locationPermissionRequestFragment_to_restaurantsMapFragment)

    }

    private fun startFragmentWithMockNavController() {
        // Create a graphical FragmentScenario for the TitleScreen
        val fragmentScenario = launchFragmentInContainer<LocationPermissionRequestFragment>()

        // Set the NavController property on the fragment
        fragmentScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }
}