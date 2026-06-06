package com.valentinerutto.rainintel.navigation

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.valentinerutto.rainintel.ui.screens.HomeScreen
import com.valentinerutto.rainintel.ui.screens.SearchScreen
import com.valentinerutto.rainintel.ui.theme.BottomNavContentInactive
import com.valentinerutto.rainintel.ui.theme.BottomNavIndicatorInactive
import com.valentinerutto.rainintel.ui.theme.BottomNavLabelInactive
import com.valentinerutto.rainintel.ui.theme.FreshGreen
import com.valentinerutto.rainintel.ui.theme.ScreenBackground

private data class BottomNavDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

private val bottomNavDestinations = listOf(
    BottomNavDestination("home", "Home", Icons.Filled.Home),
    BottomNavDestination("search", "Search", Icons.Filled.Search),
)

private const val HOME_ROUTE = "home"
private const val SEARCH_ROUTE = "search"
private const val HOME_ROUTE_WITH_ARGS = "home?lat={lat}&lng={lng}&name={name}"

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        containerColor = ScreenBackground,
        bottomBar = {
            RainIntelBottomBar(
                destinations = bottomNavDestinations,
                selectedRoute = currentDestination?.route,
                onDestinationClick = { destination ->
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(
                route = HOME_ROUTE_WITH_ARGS,
                arguments = listOf(
                    navArgument("lat") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("lng") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("name") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                )
            ) { backStackEntry ->
                HomeScreen(
                    selectedCityLat = backStackEntry.arguments
                        ?.getString("lat")
                        ?.toDoubleOrNull(),
                    selectedCityLng = backStackEntry.arguments
                        ?.getString("lng")
                        ?.toDoubleOrNull(),
                    selectedCityName = backStackEntry.arguments?.getString("name"),
                )
            }

            composable(SEARCH_ROUTE) {
                SearchScreen(
                    onRecentCityClick = { city ->
                        navController.navigate(
                            "$HOME_ROUTE?lat=${city.lat}&lng=${city.lng}&name=${Uri.encode(city.city)}"
                        ) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun RainIntelBottomBar(
    destinations: List<BottomNavDestination>,
    selectedRoute: String?,
    onDestinationClick: (BottomNavDestination) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 24.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        destinations.forEach { destination ->
            BottomNavItem(
                destination = destination,
                selected = selectedRoute?.startsWith(destination.route) == true,
                onClick = { onDestinationClick(destination) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    destination: BottomNavDestination,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (selected) {
        Surface(
            modifier = modifier
                .height(72.dp)
                .clickable(onClick = onClick),
            shape = CircleShape,
            color = FreshGreen,
            shadowElevation = 4.dp,
        ) {
            Column(
                modifier = Modifier.padding(vertical = 9.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
            ) {
                Icon(
                    imageVector = destination.icon,
                    contentDescription = destination.label,
                    tint = Color.White,
                    modifier = Modifier.size(27.dp),
                )
                Text(
                    text = destination.label,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
            }
        }
    } else {
        Column(
            modifier = modifier
                .clickable(onClick = onClick)
                .padding(vertical = 9.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Box(
                modifier = Modifier
                    .width(18.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BottomNavIndicatorInactive),
            )
            Icon(
                imageVector = destination.icon,
                contentDescription = destination.label,
                tint = BottomNavContentInactive,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = destination.label,
                color = BottomNavLabelInactive,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
            )
        }
    }
}
