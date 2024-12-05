package com.ruicomp.gpsalarm.feature.home

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ruicomp.gpsalarm.data.fake.GpsAlarmFakeRepo
import com.ruicomp.gpsalarm.model.GpsAlarm
import com.ruicomp.gpsalarm.navigation.NavRoutes
import com.ruicomp.gpsalarm.utils.rememberFlowWithLifecycle

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    snackbarHost: SnackbarHostState,
    onNavigateToScreen: (Any) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val effect = rememberFlowWithLifecycle(viewModel.effect)
    val context = LocalContext.current

    LaunchedEffect(effect) {
        effect.collect { action ->
            when (action) {
                is HomeEffect.NavigateToDetail -> {
                    onNavigateToScreen(NavRoutes.Detail(action.id))
//                    navController.navigate(route = NavRoutes.DetailTest(action.gpsAlarm.id, action.gpsAlarm.location, action.gpsAlarm.activeDays))
                }

                is HomeEffect.ShowToats -> Toast.makeText(context, "Fetch false", Toast.LENGTH_SHORT).show()
                is HomeEffect.NavigateToScreen -> TODO()
                is HomeEffect.ShowSnackbar -> {
                    val result = snackbarHost.showSnackbar(
                        message = action.message,
                        actionLabel = "Undo",
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        // Restore the deleted item if "Undo" was clicked

                    }
                }
            }
        }
    }

//    LaunchedEffect(Unit) {
//        viewModel.getData()
//    }

    HomeScreenContent(
        modifier = modifier,
        isLoading = state.value.isLoading,
        listGpsAlarms = state.value.gpsAlarms,
        onItemClick = viewModel::onAlarmClick,
        onActiveChange = { id, isActive ->
            viewModel.sendEvent(
                HomeEvent.UpdateAlarmActive(id, isActive)
            )
        },
        onDeleteGpsAlarm = {
            viewModel.sendEventForEffect(
                HomeEvent.DeleteAlarm(it)
            )
        }
    )
}

@Composable
fun HomeScreenContent(
    isLoading: Boolean,
    listGpsAlarms: List<GpsAlarm>,
    onItemClick: (GpsAlarm) -> Unit,
    onActiveChange: (Int, Boolean) -> Unit,
    onDeleteGpsAlarm: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier
                .size(64.dp)
                .align(Alignment.Center))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items = listGpsAlarms, key = { it.id }) { gpsAlarm ->
                    GpsAlarmItem(
                        gpsAlarm = gpsAlarm,
                        onClick = { onItemClick(gpsAlarm) },
                        onActiveChange = onActiveChange,
                        onDelete = { onDeleteGpsAlarm(gpsAlarm.id) }
                    )
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun GpsAlarmItem(
    gpsAlarm: GpsAlarm,
    onClick: () -> Unit,
    onActiveChange: (Int, Boolean) -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
            .fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Log.d("dddd", "GpsAlarmItem: compose")
            Text(text = gpsAlarm.name, style = MaterialTheme.typography.titleLarge)
            Text(text = "Reminder: ${gpsAlarm.reminder}", style = MaterialTheme.typography.bodyMedium)
            gpsAlarm.location.addressLine?.let {
                Text(text = "Address: $it")
            }
            Text(text = String.format("%.5f, %.5f", gpsAlarm.location.x, gpsAlarm.location.y), style = MaterialTheme.typography.bodyMedium)
            // Add more details as needed (e.g., active days, duration, sound)
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete alarm")
                }
                Switch(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    checked = gpsAlarm.isActive,
                    onCheckedChange = { onActiveChange(gpsAlarm.id, it) }
                )
            }
//            Row(modifier = Modifier.fillMaxWidth()) {
//                IconButton(onClick = onDelete) {
//                    Icon(Icons.Default.Delete, contentDescription = "Delete alarm")
//                }
//                Spacer(modifier = Modifier.width(16.dp))
//                Switch(
//                    modifier = Modifier.weight(1f),
//                    checked = gpsAlarm.isActive,
//                    onCheckedChange = { onActiveChange(gpsAlarm.id, it) }
//                )
//            }
        }
    }
}

@Preview
@Composable
private fun PreItem() {
    val gpsAlarm = GpsAlarmFakeRepo.fakeListGpsAlarms().get(1)
    GpsAlarmItem(
        gpsAlarm = gpsAlarm,
        onClick = {},
        onActiveChange = { _, _ -> },
        onDelete = {},
    )
}