package com.assignment.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.assignment.location.LocationPermissionHelper
import com.assignment.ui.restaurantsmap.RestaurantsMapActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_location_permission.*
import org.koin.android.ext.android.inject

class LocationPermissionRequestFragment:Fragment() {

    private val locationPermissionHelper: LocationPermissionHelper by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_location_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            locationPermissionHelper.onRequestPermissionsResult(requestCode, grantResults) ->
                startActivity(Intent(requireContext(), RestaurantsMapActivity::class.java))

            locationPermissionHelper.isPermissionDenied(requireActivity()) ->
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                R.string.location_permission_denied,
                Snackbar.LENGTH_INDEFINITE).show()

            else -> Snackbar.make(requireActivity().findViewById(android.R.id.content),
                R.string.restart_app_grant_permission,
                Snackbar.LENGTH_INDEFINITE).show()
        }
    }
}