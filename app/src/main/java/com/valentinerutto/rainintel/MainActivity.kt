package com.valentinerutto.rainintel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.valentinerutto.rainintel.navigation.AppNavGraph
import com.valentinerutto.rainintel.ui.theme.RainIntelTheme
import com.valentinerutto.rainintel.util.location.DeviceLocationProvider

class MainActivity : ComponentActivity() {

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        // Permission state is checked by screens when they need location.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestLocationPermissionIfNeeded()
        setContent {
            RainIntelTheme {
                AppNavGraph()
            }
        }

    }

    private fun requestLocationPermissionIfNeeded() {
        if (!DeviceLocationProvider.hasLocationPermission(this)) {
            locationPermissionLauncher.launch(DeviceLocationProvider.LOCATION_PERMISSIONS)
        }
    }
}
