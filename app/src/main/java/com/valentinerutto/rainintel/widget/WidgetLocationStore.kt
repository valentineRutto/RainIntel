package com.valentinerutto.rainintel.widget

import android.content.Context

class WidgetLocationStore(
    context: Context
) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun saveLocationName(locationName: String) {
        if (locationName.isBlank()) return

        preferences.edit()
            .putString(KEY_LOCATION_NAME, locationName)
            .apply()
    }

    fun getLocationName(): String {
        return preferences.getString(KEY_LOCATION_NAME, null)
            ?: DEFAULT_LOCATION_NAME
    }

    private companion object {
        const val PREFERENCES_NAME = "rainintel_widget_preferences"
        const val KEY_LOCATION_NAME = "location_name"
        const val DEFAULT_LOCATION_NAME = "Current location"
    }
}
