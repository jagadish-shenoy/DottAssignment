package com.assignment.ui.permission

import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.assignment.location.LocationPermissionHelper
import com.assignment.ui.R
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule


class LocationPermissionRequestFragmentTest {

    @get:Rule
    var mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var locationPermissionHelper: LocationPermissionHelper

    @Mock
    private lateinit var navController: NavController

    @Before
    fun beforeEveryTest() {
        loadKoinModules(
            module(override = true) {
                factory { locationPermissionHelper }
            }
        )
    }

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
        startFragmentWithMockNavController().apply {
            onFragment { fragment ->
                whenever(locationPermissionHelper.isPermissionGranted()).thenReturn(true)
                fragment.onRequestPermissionsResult(
                    100,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    IntArray(PERMISSION_GRANTED)
                )
            }
        }

        Espresso.onView(withId(R.id.ready_to_grant_permission))
            .check(matches(isDisplayed()))
            .perform(click())

        verify(navController).navigate(R.id.action_locationPermissionRequestFragment_to_restaurantsMapFragment)

    }

    @Test
    fun verifyPermissionDeniedErrorMessageDisplayed() {
        startFragmentWithMockNavController().apply {
            onFragment { fragment ->
                fragment.onRequestPermissionsResult(
                    100,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    IntArray(0)
                )
            }
        }

        Espresso.onView(withText(R.string.location_permission_denied)).check(matches(isDisplayed()))
    }

    @Test
    fun verifyPermissionDeniedDontAskErrorMessageDisplayed() {
        //mock permission denied + don't ask again
        whenever(locationPermissionHelper.isPermissionDeniedAndDontAskAgain(anyOrNull())).thenReturn(
            true
        )

        startFragmentWithMockNavController().apply {
            onFragment { fragment ->
                fragment.onRequestPermissionsResult(
                    100,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    IntArray(0)
                )
            }
        }

        Espresso.onView(withText(R.string.location_permission_denied_dont_ask))
            .check(matches(isDisplayed()))
        Espresso.onView(withText(R.string.location_permission_denied_go_to_settings))
            .check(matches(isDisplayed()))
    }

    private fun startFragmentWithMockNavController(): FragmentScenario<LocationPermissionRequestFragment> {
        // Create a graphical FragmentScenario for the TitleScreen
        val fragmentScenario =
            launchFragmentInContainer<LocationPermissionRequestFragment>(themeResId = R.style.AppTheme)

        // Set the NavController property on the fragment
        fragmentScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        return fragmentScenario
    }
}