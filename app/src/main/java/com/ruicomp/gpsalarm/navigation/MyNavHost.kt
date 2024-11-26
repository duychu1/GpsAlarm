package com.ruicomp.gpsalarm.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Constraints
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ruicomp.gpsalarm.Constants
import com.ruicomp.gpsalarm.feature.detail.DetailScreen
import com.ruicomp.gpsalarm.feature.home.HomeScreen
import com.ruicomp.gpsalarm.feature.maps.MapsScreen
import com.ruicomp.gpsalarm.model.MapsToDetailResult

@Composable
fun MyNavHost(
    modifier: Modifier = Modifier,
    snackbarHost: SnackbarHostState,
    navController: NavHostController = rememberNavController(),

    ) {
    NavHost(navController = navController, startDestination = NavRoutes.Detail(4)) {
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
            val mapsResult = it.savedStateHandle.get<MapsToDetailResult>(Constants.KEY_FROM_MAPS)
            DetailScreen(
                modifier = modifier,
                mapsResult = mapsResult,
                onNavigateToScreen = {
                    navController.navigate(it)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<NavRoutes.Maps>() {
            MapsScreen(
                modifier = modifier,
                onBackDetail = { lat, lng, radius, addressLine ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(Constants.KEY_FROM_MAPS, MapsToDetailResult(lat, lng, radius, addressLine))
                    navController.popBackStack()
                }
            )
        }
    }
}
