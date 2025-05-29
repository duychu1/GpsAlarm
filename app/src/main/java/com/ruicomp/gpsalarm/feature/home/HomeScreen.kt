package com.ruicomp.gpsalarm.feature.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ruicomp.gpsalarm.model.GpsAlarm
import com.ruicomp.gpsalarm.utils.rememberFlowWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.ruicomp.gpsalarm.BuildConfig
import com.ruicomp.gpsalarm.Constants
import com.ruicomp.gpsalarm.R
import com.ruicomp.gpsalarm.data.fake.GpsAlarmFakeRepo
import com.ruicomp.gpsalarm.feature.ads.BannerAdvertView
import com.ruicomp.gpsalarm.feature.outer.PremiumActivity
import com.ruicomp.gpsalarm.navigation.NavRoutes
import com.ruicomp.gpsalarm.ui.theme.Gold
import com.ruicomp.gpsalarm.ui.theme.TemplateTheme
import com.ruicomp.gpsalarm.utils.GpsCheckAndRequest
import com.ruicomp.gpsalarm.utils.PermissionUtils
import com.ruicomp.gpsalarm.utils.RequestPermissions
import com.ruicomp.gpsalarm.utils.isGpsEnabled

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
    var isShowPermissionRequest by remember { mutableStateOf(false) }

    LaunchedEffect(effect) {
        effect.collect { action ->
            when (action) {
                is HomeEffect.NavigateToDetail -> {
                    onNavigateToScreen(NavRoutes.Detail(action.id, null,null,0,null))
//                    navController.navigate(route = NavRoutes.DetailTest(action.gpsAlarm.id, action.gpsAlarm.location, action.gpsAlarm.activeDays))
                }

                is HomeEffect.ShowToats -> Toast.makeText(context, action.msg, Toast.LENGTH_SHORT).show()
                is HomeEffect.NavigateToScreen -> TODO()
                is HomeEffect.ShowSnackbar -> {
                    val result = snackbarHost.showSnackbar(
                        message = action.message,
                        actionLabel = context.getString(R.string.undo),
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                    when (result) {
                        SnackbarResult.Dismissed -> {
                            viewModel.onDbDeleteAlarm()
                        }
                        SnackbarResult.ActionPerformed -> {
                            viewModel.onClickUndoDelete()
                        }
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
        onActiveChange = { alarm, isActive ->
            if (PermissionUtils.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION)
                && isGpsEnabled(context)
                ) {
                viewModel.onAlarmActiveChange(context, alarm, isActive)
            } else {
                isShowPermissionRequest = true
            }
        },
        onDeleteGpsAlarm = viewModel::onClickDeleteAlarm,
        onNavigateToMaps = {
            onNavigateToScreen(NavRoutes.Maps(null, null, null, 500, null))
        },
        onClickDuplicate = viewModel::onDuplicateAlarm,
        onClickPin = viewModel::onClickPin,
        onNavigateToPremium = {
            context.startActivity(Intent(context, PremiumActivity::class.java).apply {
                putExtra(Constants.KEY_FROM_HOME, true)
            })
        }
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        RequestPermissions(
            permissions = listOf(
                Manifest.permission.POST_NOTIFICATIONS,
            ),
            permissionNameDisplay = stringResource(R.string.post_notification)
        )
    }

    if (isShowPermissionRequest) {
        RequestPermissions(
            permissions = listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            permissionNameDisplay = stringResource(R.string.location)
        )

        GpsCheckAndRequest(onGpsEnabled = {
            isShowPermissionRequest = false
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    isLoading: Boolean,
    listGpsAlarms: List<GpsAlarm>,
    onItemClick: (GpsAlarm) -> Unit,
    onActiveChange: (GpsAlarm, Boolean) -> Unit,
    onDeleteGpsAlarm: (GpsAlarm, Int) -> Unit,
    onClickDuplicate: (GpsAlarm) -> Unit,
    onClickPin: (GpsAlarm) -> Unit,
    onNavigateToMaps: () -> Unit,
    onNavigateToPremium: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        val context  = LocalContext.current
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            },
            navigationIcon = {
                IconButton(onClick = {

                }) {
                    Icon(Icons.Filled.Menu, contentDescription = stringResource(R.string.menu))
                }
            },
            windowInsets = WindowInsets(0, 0, 0, 0),
            actions = {
                Icon(
                    painter = painterResource(R.drawable.ic_premium),
                    contentDescription = stringResource(R.string.icon_premium),
                    modifier = Modifier
                        .size(42.dp)
                        .clickable {
                            onNavigateToPremium()
                        },
                    tint = Gold
                )
            }
        )
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        if (listGpsAlarms.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillParentMaxSize()
                                    .padding(16.dp), // Optional padding
                                contentAlignment = Alignment.Center
                            ) {
                                Text(stringResource(R.string.add_first_alarm))
                            }
                        }
                    }
                    itemsIndexed(
                        items = listGpsAlarms,
                        key = { _, item -> item.id }
                    ) { index, item ->
                        GpsAlarmItem(
                            modifier = Modifier.animateItem(),
                            gpsAlarm = item,
                            onClick = { onItemClick(item) },
                            onActiveChange = { _, isActive -> onActiveChange(item, isActive) },
                            onDelete = { onDeleteGpsAlarm(item, index) },
                            onClickDuplicate = { onClickDuplicate(item) },
                            onClickPin = { onClickPin(item) },
                        )
                    }
                }
            }
            FloatingActionButton(
                modifier = Modifier
                    .padding(end = 32.dp, bottom = 64.dp)
                    .size(64.dp)
                    .align(Alignment.BottomEnd),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = onNavigateToMaps,
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add), modifier = Modifier.size(36.dp))
            }
        }

        // In HomeScreenContent or HomeScreen
        val adIds = remember { listOf(BuildConfig.banner_home_high, BuildConfig.banner_home) }
        BannerAdvertView(adUnitId = adIds)
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun GpsAlarmItem(
    modifier: Modifier = Modifier,
    gpsAlarm: GpsAlarm,
    onClick: () -> Unit,
    onActiveChange: (Int, Boolean) -> Unit,
    onDelete: () -> Unit,
    onClickDuplicate: () -> Unit,
    onClickPin: () -> Unit,
) {
    Card(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
            .fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Log.d("dddd", "GpsAlarmItem: compose")
            Text(text = gpsAlarm.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Medium)
            Text(text = gpsAlarm.reminder, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
//            Text(text = String.format("%.5f, %.5f", gpsAlarm.location.latitude, gpsAlarm.location.longitude), style = MaterialTheme.typography.bodyMedium)
            Text(text = gpsAlarm.location.addressLine ?: "", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "${gpsAlarm.radius}m", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                DotSeparator()
                Text(text = gpsAlarm.alarmSettings.getAlarmDescription(), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                DotSeparator()
                Text(text = gpsAlarm.alarmSettings.getAlarmDuration(), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onClickDuplicate) {
                    Icon(Icons.Default.ContentCopy, contentDescription = stringResource(R.string.duplicate_alarm))
                }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete_alarm))
                }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = onClickPin) {
                    Icon(
                        Icons.Default.PushPin,
                        contentDescription = stringResource(R.string.pin_alarm),
                        tint = if (gpsAlarm.isPinned) Color.Green else MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Switch(
                    checked = gpsAlarm.isActive,
                    onCheckedChange = { onActiveChange(gpsAlarm.id, it) },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
fun DotSeparator(color: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)) {
    Icon(
        imageVector = Icons.Filled.Circle,
        contentDescription = stringResource(R.string.separator),
        tint = color,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .size(4.dp)
    )
}

@Preview
@Composable
private fun PreviewHomeScreen() {
    val listGpsAlarms = GpsAlarmFakeRepo.fakeListGpsAlarms()
    TemplateTheme {
        Surface {
            HomeScreenContent(
                isLoading = false,
                listGpsAlarms = listGpsAlarms,
                onItemClick = {},
                onActiveChange = { _, _ -> },
                onDeleteGpsAlarm = { _, _ -> },
                onNavigateToMaps = {},
                onClickDuplicate = {},
                onClickPin = {},
                onNavigateToPremium = {}
            )
        }
    }

}
