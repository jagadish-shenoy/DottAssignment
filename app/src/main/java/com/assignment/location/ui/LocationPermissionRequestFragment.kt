package com.assignment.location.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.assignment.location.LocationPermissionHelper
import com.assignment.ui.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_location_permission.*
import org.koin.android.ext.android.inject

class LocationPermissionRequestFragment:Fragment() {

    private val locationPermissionHelper: LocationPermissionHelper by inject()

    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_location_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        ready_to_grant_permission.setOnClickListener {
            locationPermissionHelper.apply {
                if(!isPermissionGranted()) {
                    requestPermission(this@LocationPermissionRequestFragment)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when {
            locationPermissionHelper.isPermissionGranted() -> navController.navigate(R.id.action_locationPermissionRequestFragment_to_restaurantsMapFragment)
            locationPermissionHelper.isPermissionDenied(requireActivity()) -> showError(R.string.location_permission_denied)
            else -> showError(R.string.restart_app_grant_permission)
        }
    }

    private fun showError(@StringRes error: Int) {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            error,
            Snackbar.LENGTH_INDEFINITE
        ).show()
    }
}