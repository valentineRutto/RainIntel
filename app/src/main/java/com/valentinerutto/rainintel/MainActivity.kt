package com.valentinerutto.rainintel

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.valentinerutto.rainintel.navigation.AppNavGraph
import com.valentinerutto.rainintel.ui.theme.RainIntelTheme
import com.valentinerutto.rainintel.util.WeatherNotificationHelper

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        // WeatherAlertWorker checks permission before posting notifications.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RainIntelTheme {
                AppNavGraph()
            }
        }

        requestNotificationPermissionIfNeeded()
        handleNotificationIntent(intent)

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val permission = Manifest.permission.POST_NOTIFICATIONS
        val isGranted = ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED

        if (!isGranted) {
            notificationPermissionLauncher.launch(permission)
        }
    }

    private fun handleNotificationIntent(intent: Intent?) {
        if (intent?.action == WeatherNotificationHelper.ACTION_OPEN_WEATHER_ALERT) {
            val notificationId = intent.getIntExtra(
                WeatherNotificationHelper.EXTRA_NOTIFICATION_ID,
                WeatherNotificationHelper.UNKNOWN_NOTIFICATION_ID
            )

            if (notificationId != WeatherNotificationHelper.UNKNOWN_NOTIFICATION_ID) {
                NotificationManagerCompat.from(this).cancel(notificationId)
            }
        }
    }
}
