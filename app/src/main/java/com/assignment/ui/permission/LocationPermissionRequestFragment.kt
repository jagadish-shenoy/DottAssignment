package com.assignment.ui.permission

import android.content.Intent
import android.net.Uri.fromParts
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.assignment.location.LocationPermissionHelper
import com.assignment.ui.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_location_permission.*
import org.koin.android.ext.android.inject


class LocationPermissionRequestFragment:Fragment() {

    private val locationPermissionHelper: LocationPermissionHelper by inject()

    private val navController
        get() = requireView().findNavController()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_location_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ready_to_grant_permission.setOnClickListener {
            locationPermissionHelper.requestPermission(this@LocationPermissionRequestFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        //When user comes back from setting, this automatically land user to next screen
        if (locationPermissionHelper.isPermissionGranted()) {
            navController.navigate(R.id.action_locationPermissionRequestFragment_to_restaurantsMapFragment)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when {
            locationPermissionHelper.isPermissionGranted() -> navController.navigate(R.id.action_locationPermissionRequestFragment_to_restaurantsMapFragment)
            locationPermissionHelper.isPermissionDeniedAndDontAskAgain(requireActivity()) -> showPermissionDeniedGotoSettingsError()
            else -> showPermissionDeniedError()
        }
    }

    //Show different errors based on if the permission can be asked again

    private fun showPermissionDeniedError() {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            R.string.location_permission_denied,
            Snackbar.LENGTH_INDEFINITE
        ).show()
    }

    private fun showPermissionDeniedGotoSettingsError() {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            R.string.location_permission_denied_dont_ask,
            Snackbar.LENGTH_INDEFINITE
        ).setAction(R.string.location_permission_denied_go_to_settings) {
            gotoAppSettings()
        }.show()
    }

    private fun gotoAppSettings() {
        val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}