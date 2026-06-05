package com.valentinerutto.farmvision.ui.screens

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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valentinerutto.farmvision.data.models.ForecastDay
import com.valentinerutto.farmvision.ui.theme.BottomNavContentInactive
import com.valentinerutto.farmvision.ui.theme.BottomNavIndicatorInactive
import com.valentinerutto.farmvision.ui.theme.BottomNavLabelInactive
import com.valentinerutto.farmvision.ui.theme.DeepGreen
import com.valentinerutto.farmvision.ui.theme.FarmVisionTheme
import com.valentinerutto.farmvision.ui.theme.FieldGreen
import com.valentinerutto.farmvision.ui.theme.ForecastBorder
import com.valentinerutto.farmvision.ui.theme.FreshGreen
import com.valentinerutto.farmvision.ui.theme.InsightBorder
import com.valentinerutto.farmvision.ui.theme.InsightDot
import com.valentinerutto.farmvision.ui.theme.InsightGreen
import com.valentinerutto.farmvision.ui.theme.InsightLabel
import com.valentinerutto.farmvision.ui.theme.Mint
import com.valentinerutto.farmvision.ui.theme.RainBlue
import com.valentinerutto.farmvision.ui.theme.ScreenBackground
import com.valentinerutto.farmvision.ui.theme.SunYellow


private data class BottomNavDestination(
    val label: String,
    val icon: ImageVector,
    val selected: Boolean = false,
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) {
    val forecastDays = listOf(
        ForecastDay("Mon", "23°", Mint),
        ForecastDay("Tue", "26°", SunYellow, selected = true),
        ForecastDay("Wed", "19°", RainBlue),
        ForecastDay("Thu", "18°", RainBlue),
        ForecastDay("Fri", "22°", Mint),
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ScreenBackground,
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = { FarmVisionBottomBar() },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.statusBars)
                .verticalScroll(rememberScrollState()),
        ) {
            HomeHeader()
            WeatherHeroCard()
            SectionTitle("7-day forecast")
            ForecastRow(forecastDays)
            AiInsightCard()
            ScanFarmButton()
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun HomeHeader() {
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
                text = "FarmPulse",
                color = DeepGreen,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
            )

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
                text = "Bomet Central, Rift Valley",
                color = DeepGreen,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun WeatherHeroCard() {
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
                        text = "24°",
                        color = Color.White,
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 56.sp,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                    Text(
                        text = "Partly cloudy",
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
                WeatherStat("Humidity", "72%", Modifier.weight(1f))
                WeatherStat("Wind", "14 km/h", Modifier.weight(1f))
                WeatherStat("Rain chance", "30%", Modifier.weight(1f))
            }
        }
    }
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
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
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
private fun ForecastRow(days: List<ForecastDay>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        days.forEach { day ->
            ForecastChip(
                day = day,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ForecastChip(
    day: ForecastDay,
    modifier: Modifier = Modifier,
) {
    val background = if (day.selected) InsightGreen else Color.White
    val borderColor = if (day.selected) FreshGreen else ForecastBorder

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
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

@Composable
private fun ScanFarmButton() {
    Button(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(58.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
    ) {
        Icon(
            imageVector = Icons.Filled.CameraAlt,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = Color.White,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = "Scan my farm",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 16.sp,
            )
            Text(
                text = "Analyse trees with your camera",
                color = Color.White.copy(alpha = 0.72f),
                fontSize = 11.sp,
                lineHeight = 13.sp,
            )
        }
    }
}

@Composable
private fun FarmVisionBottomBar() {
    val destinations = listOf(
        BottomNavDestination("Home", Icons.Filled.Home, selected = true),
        BottomNavDestination("Scan", Icons.Filled.CameraAlt),
        BottomNavDestination("History", Icons.Filled.History),
        BottomNavDestination("Settings", Icons.Filled.Settings),
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.navigationBars),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        destinations.forEach { destination ->
            BottomNavItem(
                destination = destination,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    destination: BottomNavDestination,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clickable { }
            .padding(top = 12.dp, bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .width(18.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (destination.selected) FreshGreen else BottomNavIndicatorInactive),
        )
        Icon(
            imageVector = destination.icon,
            contentDescription = destination.label,
            tint = if (destination.selected) DeepGreen else BottomNavContentInactive,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = destination.label,
            color = if (destination.selected) DeepGreen else BottomNavLabelInactive,
            fontSize = 10.sp,
            fontWeight = if (destination.selected) FontWeight.SemiBold else FontWeight.Medium,
            maxLines = 1,
        )
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
private fun HomeScreenPreview() {
    FarmVisionTheme(dynamicColor = false) {
        HomeScreen()
    }
}
