package com.valentinerutto.farmvision.util.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class CurrentLocation(
    val latitude: Double,
    val longitude: Double
)

sealed interface LocationResult {
    data class Success(val location: CurrentLocation) : LocationResult
    data object PermissionDenied : LocationResult
    data object LocationUnavailable : LocationResult
    data class Error(val message: String) : LocationResult
}

class DeviceLocationProvider(
    context: Context
) {
    private val appContext = context.applicationContext

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(appContext)

    fun hasLocationPermission(): Boolean {
        return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) ||
                hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            appContext,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationResult {
        if (!hasLocationPermission()) {
            return LocationResult.PermissionDenied
        }

        return try {
            val cachedLocation = fusedLocationClient.lastLocation.await()
            val location = cachedLocation ?: getFreshLocation()

            if (location == null) {
                LocationResult.LocationUnavailable
            } else {
                LocationResult.Success(location.toCurrentLocation())
            }
        } catch (exception: Exception) {
            LocationResult.Error(
                exception.message ?: "Unable to get current location"
            )
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getFreshLocation(): Location? {
        val cancellationTokenSource = CancellationTokenSource()

        return fusedLocationClient
            .getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                cancellationTokenSource.token
            )
            .await(
                onCancel = {
                    cancellationTokenSource.cancel()
                }
            )
    }

    private fun Location.toCurrentLocation(): CurrentLocation {
        return CurrentLocation(
            latitude = latitude,
            longitude = longitude
        )
    }
}

private suspend fun Task<Location>.await(
    onCancel: () -> Unit = {}
): Location? {
    return suspendCancellableCoroutine { continuation ->

        addOnSuccessListener { location ->
            if (continuation.isActive) {
                continuation.resume(location)
            }
        }

        addOnFailureListener { exception ->
            if (continuation.isActive) {
                continuation.cancel(exception)
            }
        }

        addOnCanceledListener {
            if (continuation.isActive) {
                continuation.resume(null)
            }
        }

        continuation.invokeOnCancellation {
            onCancel()
        }
    }
}

