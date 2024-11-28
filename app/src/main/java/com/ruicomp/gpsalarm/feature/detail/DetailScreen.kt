package com.ruicomp.gpsalarm.feature.detail

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ruicomp.gpsalarm.model.GpsAlarm
import com.ruicomp.gpsalarm.utils.rememberFlowWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.ruicomp.gpsalarm.data.fake.GpsAlarmFakeRepo
import com.ruicomp.gpsalarm.model.GpsLocation
import com.ruicomp.gpsalarm.model.MapsToDetailResult
import com.ruicomp.gpsalarm.navigation.NavRoutes
import com.ruicomp.gpsalarm.ui.theme.TemplateTheme
import com.ruicomp.gpsalarm.utils.dlog

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    mapsResult: MapsToDetailResult?,
    onNavigateToScreen: (Any) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val effect = rememberFlowWithLifecycle(viewModel.effect)
    val context = LocalContext.current

    LaunchedEffect(effect) {
        effect.collect { action ->
            when (action) {
                is DetailEffect.NavigateToMaps -> {
                    onNavigateToScreen(NavRoutes.Maps(action.lat, action.lng, action.radius))
                    Log.d("DetailScreen", "Navigate to topic with id: ")
                }

                is DetailEffect.ShowToats -> Toast.makeText(
                    context,
                    "Fetch false",
                    Toast.LENGTH_SHORT
                ).show()

                is DetailEffect.NavigateToScreen -> TODO()
            }
        }
    }

    LaunchedEffect(mapsResult) {
        if (mapsResult != null) {
            viewModel.sendEvent(
                DetailEvent.UpdateFromMaps(
                    location = GpsLocation(mapsResult.lat, mapsResult.lng, addressLine = mapsResult.addressLine),
                    radius = mapsResult.radius,
                )
            )
        }
    }

    DetailScreenContent(
        modifier = modifier,
        isLoading = state.value.isLoading,
        gpsAlarm = state.value.gpsAlarm,
        onActiveChange = { id, isActive ->
            viewModel.sendEvent(
                DetailEvent.UpdateAlarmActive(id, isActive)
            )
        },
        onDeleteGpsAlarm = {
            viewModel.sendEventForEffect(
                DetailEvent.DeleteAlarm(it)
            )
        },
        onClickAddress = viewModel::onNavigateToMaps,
        onSave = {

        },
        onAlarmChange = {
            viewModel.sendEvent(
                DetailEvent.UpdateGpsAlarm(it)
            )
        },
        onBack = onNavigateBack
    )
}

@Composable
fun DetailScreenContent(
    isLoading: Boolean,
    gpsAlarm: GpsAlarm?,
    onActiveChange: (Int, Boolean) -> Unit,
    onDeleteGpsAlarm: (Int) -> Unit,
    onClickAddress: () -> Unit,
    onAlarmChange: (GpsAlarm) -> Unit,
    onSave: (GpsAlarm) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.Center)
            )
        } else {
            if (gpsAlarm == null) {
                Text("Cannot find reminder, try again")
                return@Box
            }
            GpsAlarmItem(
                gpsAlarm = gpsAlarm,
                onActiveChange = onActiveChange,
                onDelete = { onDeleteGpsAlarm(gpsAlarm.id) },
                onClickAddress = onClickAddress,
                onSave = { },
                onAlarmChange = onAlarmChange,
                onBack = onBack
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GpsAlarmItem(
    gpsAlarm: GpsAlarm,
    onActiveChange: (Int, Boolean) -> Unit,
    onDelete: () -> Unit,
    onClickAddress: () -> Unit,
    onAlarmChange: (GpsAlarm) -> Unit,
    onSave: (GpsAlarm) -> Unit,
    onBack: () -> Unit,
) {
    val name = remember { mutableStateOf(gpsAlarm.name) }
    val reminder = remember { mutableStateOf(gpsAlarm.reminder) }
    val isActive = remember { mutableStateOf(gpsAlarm.isActive) }
    val radius = remember { mutableIntStateOf(gpsAlarm.radius) }
    val activeDays = remember { mutableStateOf(gpsAlarm.activeDays) }
    val alarmName = remember { mutableStateOf(gpsAlarm.alarmSettings.name) }
    val isRepeating = remember { mutableStateOf(gpsAlarm.alarmSettings.isRepeating) }
    val durationAlarm = remember { mutableIntStateOf(gpsAlarm.alarmSettings.duration) }
    val alarmVolume = remember { mutableFloatStateOf(gpsAlarm.alarmSettings.soundVolume) }
    val alarmVibrate = remember { mutableFloatStateOf(gpsAlarm.alarmSettings.vibrationLevel) }

    // UI
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = { Text("Edit GPS Alarm", style = MaterialTheme.typography.titleLarge) },
            navigationIcon = {
                IconButton(
                    onClick = onBack
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
//                Text("Delete", modifier = Modifier.clickable { onDelete() })
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Name
        TextField(
            value = name.value,
            onValueChange = { name.value = it },
            label = { Text("Alarm Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Reminder
        TextField(
            value = reminder.value,
            onValueChange = { reminder.value = it },
            label = { Text("Reminder") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Address")
        Column(
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClickAddress)
        ) {
            gpsAlarm.location.addressLine?.let {
                Text(text = it)
            }
            Text(text = String.format("%.5f, %.5f", gpsAlarm.location.x, gpsAlarm.location.y),)
        }

        // Active (Checkbox)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Active")
            Switch(
                checked = gpsAlarm.isActive,
                onCheckedChange = {
                    onAlarmChange(gpsAlarm.copy(isActive = it))
//                    isActive.value = it
//                    onActiveChange(gpsAlarm.id, it)
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Radius (Slider)
        Text("Radius (meters): ${gpsAlarm.radius}")

        val listRadius = listOf(50, 100, 250, 500, 750, 1000)
        // Slider value is represented as a float between 0f and (listRadius.size - 1)
        val sliderValue = listRadius.indexOf(gpsAlarm.radius).toFloat()

        CustomSlider(
            value = sliderValue,
            maxRange = (listRadius.size - 1).toFloat(),
            steps = listRadius.size - 2,
            onValueChange = {
//                radius.intValue = listRadius[it.toInt()]
                onAlarmChange(gpsAlarm.copy(radius = listRadius[it.toInt()]))
                dlog("onValueChange: ${radius.intValue}")

            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Alarm Setting", style = MaterialTheme.typography.titleLarge)

        Text(text = "Volume:")
        CustomSlider(
            value = alarmVolume.floatValue,
            maxRange = 1f,
            steps = 2,
            onValueChange = {
                alarmVolume.floatValue = it
                dlog("onValueChange: ${alarmVolume.floatValue}")
            }
        )

        Text(text = "Vibrate level:")
        CustomSlider(
            value = alarmVibrate.floatValue,
            maxRange = 1f,
            steps = 2,
            onValueChange = {
                alarmVibrate.floatValue = it
                dlog("onValueChange: ${alarmVibrate.floatValue}")
            }
        )

        // Repeating (Checkbox)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Repeating")
            Switch(
                checked = isRepeating.value,
                onCheckedChange = { isRepeating.value = it }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Alarm duration: ${durationAlarm.intValue}s")
        // Duration (TextField)
        val listDurations = listOf(10, 20, 30, 60, 90, 120)

        FlowRow {
            listDurations.forEach { duration ->
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = if (durationAlarm.intValue == duration) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        contentColor = if (durationAlarm.intValue == duration) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    ),
                    border = if (durationAlarm.intValue == duration) null else CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clickable {
                            durationAlarm.intValue = duration
                        }
                ) {
                    Text(
                        text = "${duration}s",
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))

            }
        }


        Spacer(modifier = Modifier.height(8.dp))


        Spacer(modifier = Modifier.height(8.dp))


        // Active Days (Multiple Select)
        Text("Active Days")
        val dayOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

        FlowRow(
            modifier = Modifier.padding(top = 4.dp),
        ) {
            dayOfWeek.forEachIndexed { index, day ->
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = if (activeDays.value.contains(index)) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        contentColor = if (activeDays.value.contains(index)) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    ),
                    border = if (activeDays.value.contains(index)) null else CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clickable {
                            if (activeDays.value.contains(index)) {
                                activeDays.value = activeDays.value - index
                            } else {
                                activeDays.value = activeDays.value + index
                            }
                        }
                ) {
                    Text(
                        text = day,
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp)
                    )
                }

                Spacer(Modifier.width(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Alarm Sound (TextField for file URI or path)
        TextField(
            value = alarmName.value,
            onValueChange = { alarmName.value = it },
            label = { Text("Alarm Sound Path/URI") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
                onSave(
                    gpsAlarm.copy(
                        name = name.value,
                        reminder = reminder.value,
                        isActive = isActive.value,
                        radius = radius.value,
                        activeDays = activeDays.value,
                        alarmSettings = gpsAlarm.alarmSettings.copy(
                            name = alarmName.value,
                            isRepeating = isRepeating.value,
                            duration = durationAlarm.value,
                        )
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CustomSlider(
    value: Float,
    maxRange: Float,
    steps: Int,
    onValueChange: (Float) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = 0f..maxRange, // Range from 0 to list size - 1
        steps = steps, // To get discrete steps
        onValueChangeFinished = {
            // Optionally do something when the value change finishes
        },
        thumb = {
            SliderDefaults.Thumb(
                interactionSource = interactionSource,
                thumbSize = DpSize(20.dp, 20.dp),
            )
        },
        track = {
            SliderDefaults.Track(
                sliderState = it,
                thumbTrackGapSize = 0.dp,
            )
        }
    )
}


@Preview
@Composable
private fun PreviewDetailScreen() {
    val gpsAlarm = GpsAlarmFakeRepo.fakeListGpsAlarms().get(2)
    TemplateTheme {
        Surface(Modifier.fillMaxSize()) {
            GpsAlarmItem(
                gpsAlarm = gpsAlarm,
                onActiveChange = { _, _ -> },
                onDelete = { },
                onClickAddress = {},
                onAlarmChange = {},
                onSave = { },
                onBack = { }
            )
        }
    }
}