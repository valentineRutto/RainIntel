package com.valentinerutto.rainintel.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Minimize
import androidx.compose.material.icons.outlined.Thunderstorm
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valentinerutto.rainintel.R
import com.valentinerutto.rainintel.data.local.WeatherEntity
import com.valentinerutto.rainintel.data.models.ForecastDay
import com.valentinerutto.rainintel.data.models.WeatherUiData
import com.valentinerutto.rainintel.ui.WeatherViewModel
import com.valentinerutto.rainintel.ui.theme.BottomNavContentInactive
import com.valentinerutto.rainintel.ui.theme.BottomNavIndicatorInactive
import com.valentinerutto.rainintel.ui.theme.BottomNavLabelInactive
import com.valentinerutto.rainintel.ui.theme.DeepGreen
import com.valentinerutto.rainintel.ui.theme.RainIntelTheme
import com.valentinerutto.rainintel.ui.theme.FieldGreen
import com.valentinerutto.rainintel.ui.theme.ForecastBorder
import com.valentinerutto.rainintel.ui.theme.FreshGreen
import com.valentinerutto.rainintel.ui.theme.InsightBorder
import com.valentinerutto.rainintel.ui.theme.InsightDot
import com.valentinerutto.rainintel.ui.theme.InsightGreen
import com.valentinerutto.rainintel.ui.theme.InsightLabel
import com.valentinerutto.rainintel.ui.theme.Mint
import com.valentinerutto.rainintel.ui.theme.RainBlue
import com.valentinerutto.rainintel.ui.theme.ScreenBackground
import com.valentinerutto.rainintel.ui.theme.SunYellow
import com.valentinerutto.rainintel.util.location.DeviceLocationProvider
import com.valentinerutto.rainintel.util.location.LocationNameResult
import com.valentinerutto.rainintel.util.location.LocationSettingsResult
import com.valentinerutto.rainintel.util.location.LocationResult
import com.valentinerutto.rainintel.util.toWeatherMarkerColor
import com.valentinerutto.rainintel.util.updatedTimeLabel
import com.valentinerutto.rainintel.util.withSelectedIndex
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    weatherViewModel: WeatherViewModel = koinViewModel(),
) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val uiState by weatherViewModel.uiState.collectAsState()

    var locationErrorMessage by remember { mutableStateOf<String?>(null) }
    var showTurnOnGpsAction by remember { mutableStateOf(false) }
    var locationName by remember { mutableStateOf("Current location") }

    val locationProvider = remember(context) { DeviceLocationProvider(context) }

    fun showLocationError(message: String, canTurnOnGps: Boolean = false) {
        locationErrorMessage = message
        showTurnOnGpsAction = canTurnOnGps
    }

    fun clearLocationError() {
        locationErrorMessage = null
        showTurnOnGpsAction = false
    }

    val locationSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->

        clearLocationError()
        
        if (result.resultCode == Activity.RESULT_OK) {
            coroutineScope.launch {
                loadWeatherFromCurrentLocation(
                    locationProvider = locationProvider,
                    weatherViewModel = weatherViewModel,
                    onLocationNameChanged = { name -> locationName = name },
                    onLocationError = ::showLocationError
                )
            }
        } else {
            showLocationError(
                message = context.getString(R.string.turn_on_gps_to_load_weather_for_your_current_location),
                canTurnOnGps = true
            )
        }
    }

    fun showTurnOnGpsPrompt() {
        coroutineScope.launch {
            when (val settingsResult = locationProvider.getLocationSettingsResult()) {
                LocationSettingsResult.Enabled -> {
                    clearLocationError()
                    loadWeatherFromCurrentLocation(
                        locationProvider = locationProvider,
                        weatherViewModel = weatherViewModel,
                        onLocationNameChanged = { name -> locationName = name },
                        onLocationError = ::showLocationError
                    )
                }

                is LocationSettingsResult.ResolutionRequired -> {
                    locationSettingsLauncher.launch(
                        IntentSenderRequest.Builder(settingsResult.intentSender).build()
                    )
                }

                is LocationSettingsResult.Error -> {
                    showLocationError(settingsResult.message)
                }
            }
        }
    }

    fun refreshWeather() {
        if (!locationProvider.hasLocationPermission()) {
            showLocationError(context.getString(R.string.location_permission_is_required_to_load_local_weather))
            return
        }

        coroutineScope.launch {
            
            loadWeatherFromCurrentLocation(
                locationProvider = locationProvider,
                weatherViewModel = weatherViewModel,
                onLocationNameChanged = { name -> locationName = name },
                onLocationError = ::showLocationError
            )
        }
    }

    val dashboardWeather = uiState.weather.toDashboardWeather()
    val forecastRows = uiState.weather.toDashboardForecastRows()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
            .verticalScroll(rememberScrollState()),
    ) {
        HomeHeader(
            locationName = locationName,
            isRefreshing = uiState.isLoading,
            onRefresh = ::refreshWeather
        )

        DashboardHero(weather = dashboardWeather)
        uiState.errorMessage?.let { message ->
            WeatherErrorText(message = message)
        }

        TodayDetailsCard(weather = dashboardWeather)
        SevenDayForecastCard(days = forecastRows)

        Spacer(modifier = Modifier.height(28.dp))
    }

    locationErrorMessage?.let { message ->
        LocationErrorDialog(
            message = message,
            showTurnOnGpsAction = showTurnOnGpsAction,
            onTurnOnGps = ::showTurnOnGpsPrompt,
            onDismiss = ::clearLocationError
        )
    }
}

private fun WeatherUiData?.toForecastDays(selectedIndex: Int): List<ForecastDay> {

    val dailyWeather = this?.dailyWeather.orEmpty()

    if (dailyWeather.isEmpty()) {
        return listOf(
            ForecastDay("Mon", "23°", Mint),
            ForecastDay("Tue", "26°", SunYellow),
            ForecastDay("Wed", "19°", RainBlue),
            ForecastDay("Thu", "18°", RainBlue),
            ForecastDay("Fri", "22°", Mint),
        ).withSelectedIndex(selectedIndex)

    }

    return dailyWeather.take(5).mapIndexed { index, daily ->
        ForecastDay(
            day = daily.dayOfTheWeek,
            temperature = "${daily.temp_max.toInt()}°",
            markerColor = daily.condition_code.toWeatherMarkerColor(),
            selected = index == selectedIndex
        )
    }
}

private data class DashboardWeather(
    val temperature: String,
    val condition: String,
    val highTemp: String,
    val lowTemp: String,
    val humidity: String,
    val windSpeed: String,
    val uvIndex: String,
    val icon: ImageVector,
)

private data class DashboardForecastRow(
    val day: String,
    val icon: ImageVector,
    val rainChance: String,
    val highTemp: String,
    val lowTemp: String,
    val iconTint: Color,
)

private fun WeatherUiData?.toDashboardWeather(): DashboardWeather {
    val current = this?.currentWeather
    val today = this?.dailyWeather.orEmpty().firstOrNull()
    val condition = current?.condition_code?.toDisplayCondition() ?: "Partly Cloudy"

    return DashboardWeather(
        temperature = current?.temperature?.toInt()?.let { "$it°" } ?: "--°",
        condition = condition,
        highTemp = today?.temp_max?.toInt()?.let { "H: $it°" } ?: "H: --°",
        lowTemp = today?.temp_min?.toInt()?.let { "L: $it°" } ?: "L: --°",
        humidity = today?.precipitation_probability?.let { "$it%" } ?: "--",
        windSpeed = current?.wind_speed?.toInt()?.let { "$it mph" } ?: "--",
        uvIndex = "4",
        icon = condition.toWeatherIcon()
    )
}

private fun WeatherUiData?.toDashboardForecastRows(): List<DashboardForecastRow> {
    val daily = this?.dailyWeather.orEmpty()

    if (daily.isEmpty()) {
        return listOf(
            DashboardForecastRow("Today", icon = Icons.Outlined.Minimize, "--", "--", "--", DeepGreen),
      )
    }

    return daily.take(7).mapIndexed { index, day ->
        val condition = day.condition_code.toDisplayCondition()
        DashboardForecastRow(
            day = if (index == 0) "Today" else day.dayOfTheWeek,
            icon = condition.toWeatherIcon(),
            rainChance = "${day.precipitation_probability}%",
            highTemp = "${day.temp_max.toInt()}°",
            lowTemp = "${day.temp_min.toInt()}°",
            iconTint = day.condition_code.toWeatherMarkerColor()
        )
    }
}

private fun String.toDisplayCondition(): String {
    if (isBlank()) return "Partly Cloudy"

    return lowercase()
        .replace('_', ' ')
        .split(" ")
        .filter { it.isNotBlank() }
        .joinToString(" ") { word ->
            word.replaceFirstChar { char -> char.uppercase() }
        }
}

private fun String.toWeatherIcon(): ImageVector {
    val condition = lowercase()
    return when {
        "rain" in condition || "shower" in condition || "storm" in condition -> Icons.Outlined.Thunderstorm
        "cloud" in condition || "overcast" in condition -> Icons.Outlined.Cloud
        else -> Icons.Outlined.WbSunny
    }
}

@Composable
private fun DashboardHero(
    weather: DashboardWeather,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(RainBlue.copy(alpha = 0.16f))
            .padding(horizontal = 22.dp, vertical = 54.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = weather.icon,
            contentDescription = null,
            tint = DeepGreen,
            modifier = Modifier.size(92.dp),
        )
        Text(
            text = weather.temperature,
            color = FieldGreen,
            fontSize = 72.sp,
            fontWeight = FontWeight.Light,
            lineHeight = 78.sp,
            modifier = Modifier.padding(top = 34.dp),
        )
        Text(
            text = weather.condition,
            color = FreshGreen,
            fontSize = 30.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            modifier = Modifier.padding(top = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = weather.highTemp,
                color = FieldGreen.copy(alpha = 0.82f),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = weather.lowTemp,
                color = FieldGreen.copy(alpha = 0.82f),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun TodayDetailsCard(
    weather: DashboardWeather,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 22.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.88f),
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 26.dp)) {
            Text(
                text = "TODAY'S DETAILS",
                color = FieldGreen,
                fontSize = 22.sp,
                letterSpacing = 0.sp,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 26.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                DetailTile(
                    label = "Humidity",
                    value = weather.humidity,
                    icon = Icons.Outlined.WaterDrop,
                    modifier = Modifier.weight(1f),
                )
                DetailTile(
                    label = "Wind",
                    value = weather.windSpeed,
                    icon = Icons.Outlined.Air,
                    modifier = Modifier.weight(1f),
                )
                DetailTile(
                    label = "UV Index",
                    value = weather.uvIndex,
                    icon = Icons.Outlined.WbSunny,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun DetailTile(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(10.dp),
        color = ForecastBorder.copy(alpha = 0.56f),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = FreshGreen,
                modifier = Modifier.size(28.dp),
            )
            Text(
                text = label,
                color = FieldGreen.copy(alpha = 0.74f),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = value,
                color = FieldGreen,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun SevenDayForecastCard(
    days: List<DashboardForecastRow>,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 20.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.88f),
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 26.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "7-DAY FORECAST",
                    color = FieldGreen,
                    fontSize = 22.sp,
                    letterSpacing = 0.sp,
                )
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = FieldGreen,
                    modifier = Modifier.size(20.dp),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            days.forEachIndexed { index, day ->
                ForecastListRow(day = day)
                if (index != days.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = ForecastBorder,
                        thickness = 1.dp,
                    )
                }
            }
        }
    }
}

@Composable
private fun ForecastListRow(
    day: DashboardForecastRow,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = day.day,
            color = FieldGreen,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(112.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Icon(
            imageVector = day.icon,
            contentDescription = null,
            tint = day.iconTint,
            modifier = Modifier.size(30.dp),
        )
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(start = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.WaterDrop,
                contentDescription = null,
                tint = FreshGreen,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = day.rainChance,
                color = FreshGreen,
                fontSize = 14.sp,
                maxLines = 1,
            )
        }
        Text(
            text = day.highTemp,
            color = FieldGreen,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(48.dp),
        )
        Text(
            text = day.lowTemp,
            color = FieldGreen.copy(alpha = 0.82f),
            fontSize = 22.sp,
            modifier = Modifier.width(48.dp),
        )
    }
}



private suspend fun loadWeatherFromCurrentLocation(
    locationProvider: DeviceLocationProvider,
    weatherViewModel: WeatherViewModel,
    onLocationNameChanged: (String) -> Unit,
    onLocationError: (message: String, canTurnOnGps: Boolean) -> Unit
) {

    when (val locationResult = locationProvider.getCurrentLocation()) {

        is LocationResult.Success -> {
            when (val nameResult = locationProvider.getLocationName(locationResult.location)) {
                is LocationNameResult.Success -> {
                    onLocationNameChanged(nameResult.name)
                }

                LocationNameResult.Unavailable -> Unit
                is LocationNameResult.Error -> Unit
            }

            weatherViewModel.loadWeather(
                lat = locationResult.location.latitude,
                lon = locationResult.location.longitude
            )
        }

        LocationResult.PermissionDenied -> {
            onLocationError("Location permission is required to load local weather", false)
        }

        LocationResult.LocationUnavailable -> {
            onLocationError("Turn on GPS to load weather for your current location", true)
        }

        is LocationResult.Error -> {
            onLocationError(locationResult.message, true)
        }
    }
}

@Composable
private fun LocationErrorDialog(
    message: String,
    showTurnOnGpsAction: Boolean,
    onTurnOnGps: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = if (showTurnOnGpsAction) onTurnOnGps else onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
            ) {
                Text(text = if (showTurnOnGpsAction) "Turn on GPS" else "OK")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = FieldGreen),
            ) {
                Text(text = "Dismiss")
            }
        },
        title = {
            Text(
                text = "Location unavailable",
                color = DeepGreen,
                fontWeight = FontWeight.SemiBold,
            )
        },
        text = {
            Text(
                text = message,
                color = FieldGreen,
                fontSize = 13.sp,
            )
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
    )
}

@Composable
private fun HomeHeader(
    locationName: String,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "RainIntel",
                color = DeepGreen,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
            )
            IconButton(
                onClick = onRefresh,
                enabled = !isRefreshing,
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Refresh weather",
                    tint = if (isRefreshing) FieldGreen.copy(alpha = 0.48f) else FreshGreen,
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        Row(
            modifier = Modifier.padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = FreshGreen,
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = locationName,
                color = DeepGreen,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun WeatherHeroCard(weather: WeatherUiData?) {
    val currentWeather = weather?.currentWeather
    val temperature = currentWeather?.temperature?.toInt()?.let { "$it°" } ?: "--°"
    val condition = currentWeather?.condition_code?.takeIf { it.isNotBlank() } ?: "Cached weather"
    val windSpeed = currentWeather?.let { "${it.wind_speed} km/h" } ?: "--"

    val rainChance = weather?.dailyWeather?.firstOrNull()?.let {
        "${it.precipitation_probability}%"
    } ?: "--"

    val updatedTime = currentWeather?.updatedTimeLabel() ?: "--"


    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        color = DeepGreen,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column {
                    Text(
                        text = "CURRENT CONDITIONS",
                        color = Color.White.copy(alpha = 0.74f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = temperature,
                        color = Color.White,
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 56.sp,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                    Text(
                        text = condition,
                        color = Color.White.copy(alpha = 0.84f),
                        fontSize = 14.sp,
                    )
                }

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WbSunny,
                        contentDescription = null,
                        tint = SunYellow,
                        modifier = Modifier.size(30.dp),
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(top = 16.dp),
                color = Color.White.copy(alpha = 0.18f),
                thickness = 0.5.dp,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                WeatherStat("Wind", windSpeed, Modifier.weight(1f))
                WeatherStat("Rain chance", rainChance, Modifier.weight(1f))
                WeatherStat("Updated", updatedTime, Modifier.weight(1f))
            }
        }
    }
}



@Composable
private fun WeatherErrorText(message: String) {
    Text(
        text = message,
        color = FieldGreen,
        fontSize = 12.sp,
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp),
    )
}

@Composable
private fun WeatherStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.64f),
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = DeepGreen,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 8.dp),
    )
}

@Composable
private fun ForecastRow(
    days: List<ForecastDay>,
    onForecastClick: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        days.forEachIndexed { index, day ->
            ForecastChip(
                day = day,
                onClick = { onForecastClick(index) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ForecastChip(
    day: ForecastDay,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val background = if (day.selected) InsightGreen else Color.White
    val borderColor = if (day.selected) FreshGreen else ForecastBorder

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(background)
            .border(0.5.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 4.dp, vertical = 9.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = day.day,
            color = DeepGreen,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )
        Box(
            modifier = Modifier
                .padding(vertical = 6.dp)
                .size(16.dp)
                .clip(CircleShape)
                .background(day.markerColor),
        )
        Text(
            text = day.temperature,
            color = FieldGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}

@Composable
private fun AiInsightCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(16.dp),
        color = InsightGreen,
        border = BorderStroke(0.5.dp, InsightBorder),
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(InsightDot),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "AI INSIGHT",
                    color = InsightLabel,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                text = "Light showers expected Wednesday - ideal to delay spraying. Tuesday's warmth favours tea leaf growth. Soil moisture currently adequate.",
                color = FieldGreen,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
private fun HomeScreenPreview() {
    RainIntelTheme(dynamicColor = false) {
        HomeScreen()
    }
}
