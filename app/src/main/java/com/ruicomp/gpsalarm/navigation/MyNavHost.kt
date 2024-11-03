package com.ruicomp.gpsalarm.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ruicomp.gpsalarm.feature.detail.DetailScreen
import com.ruicomp.gpsalarm.feature.home.HomeScreen
import com.ruicomp.gpsalarm.feature.maps.MapsScreen

@Composable
fun MyNavHost(
    modifier: Modifier = Modifier,
    snackbarHost: SnackbarHostState,
    navController: NavHostController = rememberNavController()

) {
    NavHost(navController = navController, startDestination = NavRoutes.Maps(null, null, 100f)) {
        composable<NavRoutes.Home> {
            HomeScreen(
                modifier = modifier,
                snackbarHost = snackbarHost,
                onNavigateToScreen = {
                    navController.navigate(it)
                }
            )
        }
        composable<NavRoutes.Detail>(
//            typeMap = mapOf(typeOf<GpsAlarm>() to parcelableType<GpsAlarm>())
        ) {
            DetailScreen(modifier = modifier)
        }

        composable<NavRoutes.Maps>() {
            MapsScreen(modifier = modifier)
        }
    }
}
