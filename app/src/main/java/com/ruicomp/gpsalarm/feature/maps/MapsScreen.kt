package com.ruicomp.gpsalarm.feature.maps

import com.ruicomp.gpsalarm.R
import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.DefaultMapUiSettings
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.ruicomp.gpsalarm.data.DefaultValue
import com.ruicomp.gpsalarm.model.PlaceAutoComplete
import com.ruicomp.gpsalarm.ui.theme.TemplateTheme
import com.ruicomp.gpsalarm.utils.GpsCheckAndRequest
import com.ruicomp.gpsalarm.utils.RequestPermissions
import com.ruicomp.gpsalarm.utils.dlog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MapsScreen(
    modifier: Modifier = Modifier,
    onBackDetail: (Int?, Double, Double, Int, String?) -> Unit,
    onFinish: () -> Unit,
    viewModel: MapsViewModel = hiltViewModel(),
) {
    val state = viewModel.mapUiState.collectAsStateWithLifecycle()
//    val effect = rememberFlowWithLifecycle(viewModel.effect)
    val context = LocalContext.current

//    LaunchedEffect(effect) {
//        effect.collect { action ->
//            when (action) {
//                is MapsEffect.ShowToast -> Toast.makeText(context, "Fetch false", Toast.LENGTH_SHORT).show()
//                is MapsEffect.NavigateToScreen -> TODO()
//            }
//        }
//    }

//    LaunchedEffect(Unit) {
//        viewModel.initData(gpsAlarm)
//    }

    MapsScreenContent(
        modifier = modifier,
        currentLocation = state.value.currentLocation,
        selectedLatLng = state.value.selectedLatLng,
        selectedAddressLine = state.value.selectedAddressLine,
        firstCameraPosition = state.value.defaultCamPos,
        zoom = state.value.zoom,
        firstLatLngBounds = state.value.defaultLatLngBounds,
        radius = state.value.radius,
        isMarkerVisible = state.value.isMarkerVisible,
        onRadiusChanged = viewModel::onRadiusChanged,
        onMapClicked = viewModel::onMapClicked,
        onBoundsChange = viewModel::onBoundChange,
        onCameraPositionChanged = viewModel::onCameraPositionChanged,
        listAddress = state.value.listPlaces,
        onSearchPlace = viewModel::onSearchAddress,
        onSelectPlace = viewModel::onSelectedPlace,
        onFocusMyLocation = {viewModel.onFocusMyLocation(context)},
        onClickSave = {
            if (state.value.selectedLatLng == null || state.value.selectedAddressLine == null) {
                Toast.makeText(context, "Location not selected", Toast.LENGTH_SHORT).show()
                return@MapsScreenContent
            }

            onBackDetail(
                state.value.alarmId,
                state.value.selectedLatLng!!.latitude,
                state.value.selectedLatLng!!.longitude,
                state.value.radius,
                state.value.selectedAddressLine,
            )
        },
        onFinish = onFinish,
        isDarkTheme = state.value.isDarkTheme ?: isSystemInDarkTheme(),
        onDarkThemeChanged = viewModel::onDarkThemeChanged
    )

    RequestPermissions(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ),
        permissionNameDisplay = "Location"
    )

    GpsCheckAndRequest()
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun MapsScreenContent(
    currentLocation: LatLng?,
    selectedLatLng: LatLng?,
    selectedAddressLine: String?,
    firstCameraPosition: LatLng,
    zoom: Float,
    onClickSave: () -> Unit,
    onFinish: () -> Unit,
    firstLatLngBounds: LatLngBounds,
    radius: Int,
    isMarkerVisible: Boolean,
    onRadiusChanged: (Int) -> Unit,
    onMapClicked: (LatLng) -> Unit,
    onBoundsChange: (LatLngBounds?) -> Unit,
    onCameraPositionChanged: (LatLng, Float) -> Unit,
    listAddress: List<PlaceAutoComplete>?,
    onSearchPlace: (String) -> Unit,
    onSelectPlace: (PlaceAutoComplete) -> Unit,
    onFocusMyLocation: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
) {
    val cameraPositionState = rememberCameraPositionState()
    val isFirstLaunch = rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving && !isFirstLaunch.value) {
            onCameraPositionChanged(
                cameraPositionState.position.target, cameraPositionState.position.zoom
            )
        }
    }

    LaunchedEffect(firstCameraPosition) {
        dlog("firstCameraPosition: $firstCameraPosition")
        if (isFirstLaunch.value) {
            cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(firstCameraPosition, zoom))
            isFirstLaunch.value = false
            return@LaunchedEffect
        }

        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(firstCameraPosition, zoom),
            durationMs = 1000
        )
    }

    // Select the color scheme for THIS screen
    val localColorScheme = if (isDarkTheme) {
        darkColorScheme()
    } else {
        lightColorScheme()
    }

//    val window = (LocalContext.current as Activity).window
//    val view = LocalView.current
//    WindowCompat.setDecorFitsSystemWindows(window, false)
//
//    SideEffect {
//        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
//    }
    val context = LocalContext.current
    val mapProperties = remember(isDarkTheme) {
        MapProperties(
            // Apply dark style JSON when in dark mode
            mapStyleOptions = if (isDarkTheme) {
                // Load the dark map style JSON from raw resource
                MapStyleOptions.loadRawResourceStyle(
                    context,
                    R.raw.map_dark_style
                )
            } else {
                // Use default light style (or you can load a custom light style)
                null
            }
        )
    }

    val query = remember { mutableStateOf("") }

    MaterialTheme(
        colorScheme = localColorScheme,
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = onMapClicked,
                properties = mapProperties,
                uiSettings = DefaultMapUiSettings.copy(zoomControlsEnabled = false)
            ) {

                MarkerCircleLocation(selectedLatLng, radius)


                MarkerMyLocation(currentLocation)
            }

//        SearchAddress(
//            query = query.value,
//            onQueryChange = { newQuery ->
//                query.value = newQuery
//                // Perform search action here, e.g., filter a list
//            },
//            modifier = Modifier.statusBarsPadding()
//        )

            val isMyLocationClick = remember { mutableStateOf(false) }

            LaunchedEffect(isMyLocationClick.value) {
                if (isMyLocationClick.value) {
                    isMyLocationClick.value = false
                    onFocusMyLocation()
                    CoroutineScope(Dispatchers.Unconfined).launch {
                        try {
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLngZoom(currentLocation!!, 15f),
                                durationMs = 1000
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            MyLocationIcon {
                isMyLocationClick.value = true
            }

            CircularButtonWithDropdown(
                radius = radius,
                onRadiusChanged = onRadiusChanged,
                localDarkThemeEnabled = isDarkTheme,
                onClickChangeTheme = {
                    onDarkThemeChanged(!isDarkTheme)
                }
            )


            BottomSaveAndAddress(
                selectedLatLng = selectedLatLng,
                selectedAddressLine = selectedAddressLine,
                onClickSave = onClickSave
            )

            SearchBarAddress(
                query = query.value,
                listAddress = listAddress,
                onExcSearch = onSearchPlace,
                onSelectPlace = onSelectPlace,
                onFinish = onFinish
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun BoxWithConstraintsScope.BottomSaveAndAddress(
    selectedLatLng: LatLng?,
    selectedAddressLine: String? = null,
    onClickSave: () -> Unit,
) {
    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .navigationBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .padding(bottom = 12.dp),
    ) {
        if (selectedAddressLine != null)
            Text(
                text = selectedAddressLine,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall
            )
//        if (selectedLatLng != null)
//            Text(
//                text = String.format("%.5f, %.5f", selectedLatLng.latitude, selectedLatLng.longitude),
//                color = MaterialTheme.colorScheme.onPrimary
//            )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = onClickSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 5.dp,
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Text(
                text = stringResource(R.string.save),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun MarkerCircleLocation(lng: LatLng?, radius: Int) {
    if (lng == null) return
    Marker(
        state = rememberUpdatedMarkerState(lng),
    )
    Circle(
        center = lng,
        radius = radius.toDouble(),
        strokeWidth = 2f,
        strokeColor = Color.Red,
        fillColor = Color.Red.copy(alpha = 0.2f)
    )
    dlog("MarkerCircleLocation: recompose check")

}

@Composable
private fun BoxWithConstraintsScope.MyLocationIcon(
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(end = 10.dp)
            .size(54.dp)
            .offset(y = (maxHeight * 0.7f))
            .border(1.dp, Color.LightGray, CircleShape)
            .shadow(elevation = 2.dp, shape = CircleShape)
            .background(Color.White)
    ) {
        // Your icon content here
        Icon(
            painter = painterResource(id = R.drawable.ic_my_location),
            contentDescription = "Icon Button",
            tint = Color.Blue // Change the tint as needed
        )
    }
}

@Composable
fun BoxWithConstraintsScope.CircularButtonWithDropdown(
    radius: Int,
    onRadiusChanged: (Int) -> Unit = {},
    localDarkThemeEnabled: Boolean = false,
    onClickChangeTheme: () -> Unit = {},
) {
    val expanded = remember { mutableStateOf(false) }
    val listRadius = DefaultValue.listRadius

    Column(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(end = 10.dp)
            .offset(y = (maxHeight * 0.2f))
    ) {
        //create a circular button to change maps theme
        Box(
            modifier = Modifier
                .size(50.dp)
                .border(1.dp, Color.LightGray, CircleShape)
                .shadow(elevation = 2.dp, shape = CircleShape)
                .background(Color.White)
                .clickable {
                    onClickChangeTheme()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when(localDarkThemeEnabled) {
                    true -> Icons.Default.WbSunny // Show LightMode icon if dark is forced
                    false -> Icons.Default.DarkMode // Show DarkMode icon if light is forced
                    null -> if (isSystemInDarkTheme()) Icons.Default.LightMode else Icons.Default.DarkMode // Reflect system
                },
                contentDescription = "Toggle Theme for this screen",
                tint = Color.Blue, // Adjust tint as needed
                modifier = Modifier.size(32.dp)
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .size(50.dp)
                .border(1.dp, Color.LightGray, CircleShape)
                .shadow(elevation = 2.dp, shape = CircleShape)
                .background(Color.White)
                .clickable { expanded.value = !expanded.value }
        ) {
            Text(
                text = "${radius}m",
                fontSize = 12.sp,
                color = Color.Blue,
                fontWeight = FontWeight.Medium
            )
        }

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .padding(horizontal = 4.dp)
        ) {
            listRadius.forEach { item ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = {
                            expanded.value = false
                            onRadiusChanged(item)
                        })
                ) {
                    Text(text = "${item}m", modifier = Modifier.padding(4.dp))
                }
            }
        }
    }
}

@Composable
fun SearchAddress(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(text = "Search")
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarAddress(
    modifier: Modifier = Modifier,
    query: String,
    onSelectPlace: (PlaceAutoComplete) -> Unit,
    listAddress: List<PlaceAutoComplete>?,
    onExcSearch: (String) -> Unit,
    onFinish: () -> Unit,
) {
    val text = rememberSaveable { mutableStateOf("") }
    val expanded = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(text.value) {
        if (text.value.isBlank()) return@LaunchedEffect
        // Debounce the search query
        val job = launch {
            delay(800L)
            onExcSearch(text.value)
        }

        // Cancel the previous job if a new query is entered before the delay
        snapshotFlow { text.value }.collect { newQuery ->
            job.cancel()
            job.join()
            launch {
                delay(500L)
                onExcSearch(text.value)
            }
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .semantics { isTraversalGroup = true }) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            colors = SearchBarDefaults.colors(
                MaterialTheme.colorScheme.surfaceVariant,

            ),
            inputField = {
                SearchBarDefaults.InputField(
                    query = text.value,
                    onQueryChange = { text.value = it },
                    onSearch = { expanded.value = false },
                    expanded = expanded.value,
                    onExpandedChange = { expanded.value = it },
                    placeholder = { Text(stringResource(R.string.search_3dot)) },
                    leadingIcon = {
                        IconButton(onClick = {
                            if (expanded.value) {
                                expanded.value = false
                            } else {
                                onFinish()
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Collapse search bar")
                        }
                    },
                    trailingIcon = {
                        if (expanded.value) {
                            if (text.value.isNotEmpty()) {
                                IconButton(onClick = {
                                    text.value = "" // Clear the search query
                                    println("Clear icon clicked")
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear search query")
                                }
                            }
                        }
                    },
//                    modifier = Modifier
//                        .shadow(elevation = 1.dp, shape = CircleShape)

                )
            },
            expanded = expanded.value,
            onExpandedChange = { expanded.value = it },
        ) {
            if (listAddress == null || listAddress.isEmpty() && text.value.isNotEmpty()) {
                Text(stringResource(R.string.no_results_for, text.value))
                return@SearchBar
            }

            LazyColumn {
                items(listAddress) {
                    ListItem(
                        headlineContent = { Text(it.detailAddress) },
//                        supportingContent = { Text(it.detailAddress) },
                        leadingContent = {
                            Icon(
                                Icons.Rounded.LocationOn,
                                contentDescription = null
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .clickable {
                                onSelectPlace(it)
                                expanded.value = false
                            }
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp))
                }
            }
        }


    }
}

@Composable
fun MarkerMyLocation(position: LatLng?) {
    if (position == null) return

    val state = rememberUpdatedMarkerState(position)
    MarkerComposable(
        state = state,
    ) {
        MarkerContent()

    }
    dlog("MarkerMyLocation: recompose check")
}

@Composable
private fun MarkerContent() {
    Box(
        modifier = Modifier
            .size(38.dp)  // Apply the scale animation
            .shadow(8.dp, CircleShape)
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                CircleShape
            ),  // Use the animated alpha
        contentAlignment = Alignment.Center
    ) {
        // Blue dot location indicator
        Box(
            modifier = Modifier
                .size(24.dp)// Apply the scale animation to inner circle too
                .background(Color.Blue, CircleShape)
                .border(3.dp, Color.White, CircleShape),
        )
    }
}

@Composable
fun rememberUpdatedMarkerState(newPosition: LatLng): MarkerState =
    remember { MarkerState(position = newPosition) }
        .apply { position = newPosition }


@SuppressLint("UnusedBoxWithConstraintsScope")
@Preview(name = "Light Theme Preview", showBackground = true)
@Preview(
    name = "Dark Theme Preview",
    uiMode = UI_MODE_NIGHT_YES, // This enables dark mode for the preview
    showBackground = true
)
@Composable
private fun preMaps() {
//    BoxWithConstraints(Modifier.fillMaxSize().background(Color.Cyan)) {
//        MyLocationIcon(){}
//    }
    TemplateTheme {
        Surface(Modifier.fillMaxSize()) {
            BoxWithConstraints {
                CircularButtonWithDropdown(
                    radius = 500,
                    onRadiusChanged = {}
                )
                MyLocationIcon { }
                BottomSaveAndAddress (
                    null, null ,{}
                )
                SearchBarAddress(
                    query = "query.value",
                    listAddress = emptyList(),
                    onExcSearch = {  },
                    onSelectPlace = {},
                    onFinish = {  }
                )

                Column(
                    modifier = Modifier
                        .offset(x = maxWidth*0.5f,y = (maxHeight * 0.5f))
                ) {
                    MarkerContent()
                }

            }
        }
    }
}