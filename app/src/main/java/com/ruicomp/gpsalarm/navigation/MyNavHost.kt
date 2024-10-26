package com.ruicomp.gpsalarm.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.ruicomp.gpsalarm.feature.detail.DetailScreen
import com.ruicomp.gpsalarm.feature.detail_test.DetailTestScreen
import com.ruicomp.gpsalarm.feature.home.HomeScreen
import com.ruicomp.gpsalarm.model.GpsAlarm
import com.ruicomp.gpsalarm.utils.parcelableType
import kotlin.reflect.typeOf

@Composable
fun MyNavHost(
    modifier: Modifier = Modifier,
    snackbarHost: SnackbarHostState,
    navController: NavHostController = rememberNavController()

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
        composable<NavRoutes.Detail>(
//            typeMap = mapOf(typeOf<GpsAlarm>() to parcelableType<GpsAlarm>())
        ) {
            DetailScreen(modifier = modifier)
        }
    }
}
