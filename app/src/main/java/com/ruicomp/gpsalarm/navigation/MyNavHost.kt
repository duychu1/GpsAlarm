package com.ruicomp.gpsalarm.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Constraints
import androidx.navigation.NavGraph.Companion.findStartDestination
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
    NavHost(navController = navController, startDestination = NavRoutes.Home) {
        composable<NavRoutes.Home> {
            HomeScreen(
                modifier = modifier,
                snackbarHost = snackbarHost,
                onNavigateToScreen = {
                    navController.navigate(it)
                }
            )
        }
        composable<NavRoutes.Detail> {
            val mapsResult = it.savedStateHandle.get<MapsToDetailResult>(Constants.KEY_FROM_MAPS)
            DetailScreen(
                modifier = modifier,
                mapsResult = mapsResult,
                onNavigateToScreen = {
                    navController.navigate(it) {
                        popUpTo(it) { inclusive = false }
                        restoreState = true
                        launchSingleTop = true
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<NavRoutes.Maps> {
            MapsScreen(
                modifier = modifier,
                onBackDetail = { alarmId, lat, lng, radius, addressLine ->
                    //check if detail not on backstack
                    if (alarmId == null) {
                        navController.navigate(NavRoutes.Detail(alarmId, lat, lng, radius, addressLine)) {
                            popUpTo(NavRoutes.Maps(null, null, null, 500, null)) {
                                inclusive = true
                            }
                        }
                        return@MapsScreen
                    }
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(Constants.KEY_FROM_MAPS, MapsToDetailResult(alarmId, lat, lng, radius, addressLine))
                    navController.popBackStack()
                },
                onFinish = {
                    navController.popBackStack()
                }
            )
        }
    }
}
