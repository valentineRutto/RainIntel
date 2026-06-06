package com.valentinerutto.rainintel.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Thunderstorm
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.valentinerutto.rainintel.data.local.CityEntity
import com.valentinerutto.rainintel.data.local.PreloadedCityEntity
import com.valentinerutto.rainintel.ui.WeatherViewModel
import com.valentinerutto.rainintel.ui.theme.BottomNavContentInactive
import com.valentinerutto.rainintel.ui.theme.BottomNavIndicatorInactive
import com.valentinerutto.rainintel.ui.theme.BottomNavLabelInactive
import com.valentinerutto.rainintel.ui.theme.DeepGreen
import com.valentinerutto.rainintel.ui.theme.FieldGreen
import com.valentinerutto.rainintel.ui.theme.ForecastBorder
import com.valentinerutto.rainintel.ui.theme.FreshGreen
import com.valentinerutto.rainintel.ui.theme.InsightGreen
import com.valentinerutto.rainintel.ui.theme.Mint
import com.valentinerutto.rainintel.ui.theme.RainBlue
import com.valentinerutto.rainintel.ui.theme.RainIntelTheme
import com.valentinerutto.rainintel.ui.theme.ScreenBackground
import com.valentinerutto.rainintel.util.toDisplayCondition
import org.koin.compose.viewmodel.koinViewModel

private data class SavedCity(
    val city: String,
    val country: String,
    val temperature: String,
    val condition: String,
)

private data class HourlyForecast(
    val label: String,
    val temperature: String,
    val icon: ImageVector,
    val selected: Boolean = false,
)


@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = koinViewModel(),
    onRecentCityClick: (CityEntity) -> Unit = {},
) {

    val searchQuery by viewModel.searchQuery.collectAsState()

    val state by viewModel.uiSearchState.collectAsStateWithLifecycle()

    val savedCities = listOf(
        SavedCity("Paris", "France", "62°", "Partly Cloudy"),
        SavedCity("Tokyo", "Japan", "74°", "Sunny"),
    )

    val hourlyForecast = listOf(
        HourlyForecast("Now", "58°", Icons.Outlined.Cloud),
        HourlyForecast("3 PM", "56°", Icons.Outlined.Thunderstorm, selected = true),
        HourlyForecast("4 PM", "55°", Icons.Outlined.Thunderstorm),
        HourlyForecast("5 PM", "54°", Icons.Outlined.Cloud),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
            .verticalScroll(rememberScrollState()),
    ) {
        SearchHeader(locationName = "London")
        HorizontalDivider(color = ForecastBorder)

        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            SearchInput(
                query = searchQuery,
                onQueryChange = { viewModel.setSearchQuery(it)},
            )

            if (searchQuery.isNotBlank()) {
                SectionHeader(title = "SEARCH RESULTS")
                SearchResultsList(
                    cities = state.searchResults,
                    onCityClick = viewModel::onCityClicked,
                )
            }

            SectionHeader(
                title = "RECENT SEARCHES",
                action = "Clear All",
                onActionClick = viewModel::clearRecentSearches,
            )
            RecentSearchGrid(
                cities = state.recentWeather,
                onCityClick = onRecentCityClick,
            )

            state.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = FieldGreen,
                    fontSize = 13.sp,
                )
            }

            SectionHeader(
                title = "SAVED CITIES",
                action = "Edit",
            )
            SavedCitiesList(cities = savedCities)

            CurrentWeatherCard(cityWeather = state.selectedCityWeather)

            SectionHeader(title = "HOURLY FORECAST")
            HourlyForecastRow(forecast = hourlyForecast)

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SearchHeader(
    locationName: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = FreshGreen,
                modifier = Modifier.size(26.dp),
            )
            Text(
                text = locationName,
                color = FieldGreen,
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Icon(
            imageVector = Icons.Outlined.AccountCircle,
            contentDescription = "Profile",
            tint = FieldGreen,
            modifier = Modifier.size(28.dp),
        )
    }
}

@Composable
private fun SearchInput(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {

    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .border(1.dp, ForecastBorder, RoundedCornerShape(16.dp)),
        placeholder = {
            Text(
                text = "Search city...",
                color = BottomNavContentInactive,
                fontSize = 18.sp,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = FieldGreen,
                modifier = Modifier.size(28.dp),
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Clear search",
                        tint =  BottomNavContentInactive
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = FieldGreen,
            unfocusedTextColor = FieldGreen,
        ),
    )
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: String? = null,
    onActionClick: () -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = FieldGreen,
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 0.sp,
        )
        action?.let {
            TextButton(onClick = onActionClick) {
                Text(
                    text = it,
                    color = FreshGreen,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun SearchResultsList(
    cities: List<PreloadedCityEntity>,
    onCityClick: (PreloadedCityEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (cities.isEmpty()) {
            Text(
                text = "No cities found",
                color = FieldGreen.copy(alpha = 0.72f),
                fontSize = 15.sp,
                modifier = Modifier.padding(vertical = 6.dp),
            )
        } else {
            cities.forEach { city ->
                SearchResultRow(
                    city = city,
                    onClick = { onCityClick(city) },
                )
            }
        }
    }
}

@Composable
private fun SearchResultRow(
    city: PreloadedCityEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, ForecastBorder),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = FreshGreen,
                modifier = Modifier.size(24.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = city.city,
                    color = FieldGreen,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = city.country,
                    color = FieldGreen.copy(alpha = 0.72f),
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = FieldGreen,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun RecentSearchGrid(
    cities: List<CityEntity>,
    onCityClick: (CityEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        cities.chunked(2).forEach { rowCities ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                rowCities.forEach { city ->
                    RecentCityCard(
                        city = city,
                        onClick = { onCityClick(city) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowCities.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun RecentCityCard(
    city: CityEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .height(136.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = InsightGreen,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Icon(
                imageVector = if (city.isSaved) Icons.Outlined.Star else Icons.Outlined.StarBorder,
                contentDescription = null,
                tint = if (city.isSaved) FreshGreen else BottomNavContentInactive,
                modifier = Modifier.size(24.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = city.city,
                    color = FieldGreen,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = city.country,
                    color = FieldGreen.copy(alpha = 0.68f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = city.temperature?.toInt()?.let { "$it°" } ?: "--°",
                    color = FieldGreen,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = city.condition_code?.toDisplayCondition() ?: "Weather unavailable",
                    color = FieldGreen.copy(alpha = 0.68f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun SavedCitiesList(
    cities: List<SavedCity>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        cities.forEach { city ->
            SavedCityRow(city = city)
        }
    }
}

@Composable
private fun SavedCityRow(
    city: SavedCity,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(94.dp),
        shape = RoundedCornerShape(16.dp),
        color = InsightGreen,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 22.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.Star,
                contentDescription = null,
                tint = FreshGreen,
                modifier = Modifier.size(28.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "${city.city}, ${city.country}",
                    color = FieldGreen,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${city.temperature} • ${city.condition}",
                    color = FieldGreen.copy(alpha = 0.78f),
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = FieldGreen,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

@Composable
private fun CurrentWeatherCard(
    cityWeather: CityEntity?,
    modifier: Modifier = Modifier,
) {
    val cityName = cityWeather?.city ?: "Select a city"
    val countryName = cityWeather?.country.orEmpty()
    val temperature = cityWeather?.temperature?.toInt()?.let { "$it°" } ?: "--°"
    val condition = cityWeather?.condition_code?.toDisplayCondition() ?: "Search for weather"

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(338.dp),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 4.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            RainBlue.copy(alpha = 0.78f),
                            Mint.copy(alpha = 0.55f),
                            DeepGreen.copy(alpha = 0.25f),
                        )
                    )
                ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.62f),
                                Color.Transparent,
                                FieldGreen.copy(alpha = 0.20f),
                            )
                        )
                    ),
            )

            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = "CURRENT WEATHER",
                    color = FreshGreen,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.sp,
                )
                Text(
                    text = cityName,
                    color = FieldGreen,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (countryName.isNotBlank()) {
                    Text(
                        text = countryName,
                        color = FieldGreen.copy(alpha = 0.72f),
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 34.dp, end = 24.dp),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = temperature,
                    color = FieldGreen,
                    fontSize = 76.sp,
                    fontWeight = FontWeight.Light,
                    lineHeight = 78.sp,
                )
                Text(
                    text = condition,
                    color = FieldGreen.copy(alpha = 0.75f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            RainfallRiskCard(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp),
            )
        }
    }
}

@Composable
private fun RainfallRiskCard(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFE8F4FF),
        border = BorderStroke(1.dp, RainBlue),
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = Icons.Outlined.Thunderstorm,
                contentDescription = null,
                tint = DeepGreen,
                modifier = Modifier.size(30.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "RAINFALL RISK",
                    color = DeepGreen,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.sp,
                )
                Text(
                    text = "High Risk of Rainfall: Heavy showers expected at 3 PM",
                    color = FieldGreen,
                    fontSize = 18.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun HourlyForecastRow(
    forecast: List<HourlyForecast>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        forecast.forEach { item ->
            HourlyForecastCard(forecast = item)
        }
    }
}

@Composable
private fun HourlyForecastCard(
    forecast: HourlyForecast,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .width(102.dp)
            .height(146.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = if (forecast.selected) BorderStroke(2.dp, RainBlue) else null,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = forecast.label,
                color = FieldGreen,
                fontSize = 16.sp,
                fontWeight = if (forecast.selected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1,
            )
            Icon(
                imageVector = forecast.icon,
                contentDescription = null,
                tint = FreshGreen,
                modifier = Modifier.size(30.dp),
            )
            Text(
                text = forecast.temperature,
                color = FieldGreen,
                fontSize = 22.sp,
                fontWeight = if (forecast.selected) FontWeight.Bold else FontWeight.Normal,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
private fun SearchScreenPreview() {
    RainIntelTheme(dynamicColor = false) {
        SearchScreen()
    }
}
