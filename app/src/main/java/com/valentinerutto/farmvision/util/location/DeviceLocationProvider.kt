package com.valentinerutto.farmvision.util.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.IntentSender
import android.location.Location
import android.location.Geocoder
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
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

sealed interface LocationSettingsResult {
    data object Enabled : LocationSettingsResult
    data class ResolutionRequired(val intentSender: IntentSender) : LocationSettingsResult
    data class Error(val message: String) : LocationSettingsResult
}

sealed interface LocationNameResult {
    data class Success(val name: String) : LocationNameResult
    data object Unavailable : LocationNameResult
    data class Error(val message: String) : LocationNameResult
}

class DeviceLocationProvider(
    context: Context
) {
    private val appContext = context.applicationContext

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(appContext)

    private val settingsClient = LocationServices.getSettingsClient(appContext)

    fun hasLocationPermission(): Boolean {
        return hasLocationPermission(appContext)
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

    suspend fun getLocationSettingsResult(): LocationSettingsResult {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            LOCATION_REQUEST_INTERVAL_MILLIS
        ).build()
        val settingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
            .build()

        return settingsClient.checkLocationSettings(settingsRequest).awaitSettings()
    }

    @Suppress("DEPRECATION")
    suspend fun getLocationName(location: CurrentLocation): LocationNameResult {
        return withContext(Dispatchers.IO) {
            runCatching {
                val address = Geocoder(appContext, Locale.getDefault())
                    .getFromLocation(
                        location.latitude,
                        location.longitude,
                        MAX_GEOCODER_RESULTS
                    )
                    ?.firstOrNull()

                val locationName = address?.let {
                    listOfNotNull(
                        it.locality ?: it.subLocality ?: it.subAdminArea,
                        it.adminArea ?: it.countryName
                    )
                        .distinct()
                        .joinToString(", ")
                }.orEmpty()

                if (locationName.isBlank()) {
                    LocationNameResult.Unavailable
                } else {
                    LocationNameResult.Success(locationName)
                }
            }.getOrElse { exception ->
                LocationNameResult.Error(
                    exception.message ?: "Unable to get location name"
                )
            }
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

    companion object {
        val LOCATION_PERMISSIONS: Array<String>
            get() = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

        fun hasLocationPermission(context: Context): Boolean {
            return LOCATION_PERMISSIONS.any { permission ->
                ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
    }
}

private const val LOCATION_REQUEST_INTERVAL_MILLIS = 10_000L
private const val MAX_GEOCODER_RESULTS = 1

private suspend fun Task<*>.awaitSettings(): LocationSettingsResult {
    return suspendCancellableCoroutine { continuation ->
        addOnSuccessListener {
            if (continuation.isActive) {
                continuation.resume(LocationSettingsResult.Enabled)
            }
        }

        addOnFailureListener { exception ->
            if (!continuation.isActive) return@addOnFailureListener

            if (exception is ResolvableApiException) {
                continuation.resume(
                    LocationSettingsResult.ResolutionRequired(
                        intentSender = exception.resolution.intentSender
                    )
                )
            } else {
                continuation.resume(
                    LocationSettingsResult.Error(
                        message = exception.message ?: "Unable to enable location services"
                    )
                )
            }
        }

        addOnCanceledListener {
            if (continuation.isActive) {
                continuation.resume(LocationSettingsResult.Error("Location settings request was cancelled"))
            }
        }
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
