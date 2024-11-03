package com.ruicomp.gpsalarm.feature.maps

import com.ruicomp.gpsalarm.R
import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.ruicomp.gpsalarm.model.PlaceAutoComplete
import com.ruicomp.gpsalarm.ui.theme.TemplateTheme
import com.ruicomp.gpsalarm.utils.RequestPermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MapsScreen(
    modifier: Modifier = Modifier,
    viewModel: MapsViewModel = hiltViewModel()
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
        onSearchPlace = viewModel::onSearchPlaces,
        onSelectPlace = viewModel::onSelectedPlace
    )

    RequestPermissions(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    )
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun MapsScreenContent(
    currentLocation: LatLng?,
    selectedLatLng: LatLng?,
    firstCameraPosition: LatLng,
    zoom: Float,
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
    modifier: Modifier = Modifier
) {
    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(firstCameraPosition) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(firstCameraPosition, zoom),
            durationMs = 1000
        )
    }

//    val window = (LocalContext.current as Activity).window
//    val view = LocalView.current
//    WindowCompat.setDecorFitsSystemWindows(window, false)
//
//    SideEffect {
//        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
//    }

    val query = remember { mutableStateOf("") }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            onCameraPositionChanged(
                cameraPositionState.position.target, cameraPositionState.position.zoom
            )
        }
    }
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = onMapClicked
        ) {
            selectedLatLng?.let {
                Marker(
                    state = rememberUpdatedMarkerState(it),
                )
                Circle(
                    center = it,
                    radius = radius.toDouble(),
                    strokeWidth = 2f,
                    strokeColor = Color.Red,
                    fillColor = Color.Red.copy(alpha = 0.2f)
                )
            }

            currentLocation?.let {
                MarkerMyLocation(currentLocation)
            }
        }

//        SearchAddress(
//            query = query.value,
//            onQueryChange = { newQuery ->
//                query.value = newQuery
//                // Perform search action here, e.g., filter a list
//            },
//            modifier = Modifier.statusBarsPadding()
//        )

        SearchBarAddress(
            query = query.value,
            listAddress = listAddress,
            onExcSearch = onSearchPlace,
            onSelectPlace = onSelectPlace,
        )

        MyLocationIcon {
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

        CircularButtonWithDropdown(
            radius = radius,
            onRadiusChanged = onRadiusChanged
        )

        // Add a bottom bar to adjust the radius
//        BottomAppBar(modifier = Modifier.align(Alignment.BottomCenter)) {
//            Slider(
//                value = radius,
//                onValueChange = { onRadiusChanged(it) },
//                valueRange = 0f..1000f,
//                steps = 10
//            )
//        }
    }
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
    onRadiusChanged: (Int) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }
    val listRadius = listOf(50, 100, 250, 500, 750, 1000)

    Column(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(end = 10.dp)
            .offset(y = (maxHeight * 0.2f))
    ) {
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
            Text(text = "${radius}m", fontSize = 12.sp, color = Color.Blue)
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
                    modifier = Modifier.fillMaxWidth().padding(4.dp).clickable(onClick = {
                        expanded.value = false
                        onRadiusChanged(item)
                    })
                ) {
                    Text(text = "${item}m")
                }
            }
        }
    }
}

@Composable
fun SearchAddress(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
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
    onExcSearch: (String) -> Unit
) {
    val text = rememberSaveable { mutableStateOf("") }
    val expanded = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(text.value) {
        if (text.value.isBlank()) return@LaunchedEffect
        // Debounce the search query
        val job = launch {
            delay(500L)
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

    Box(Modifier
        .fillMaxSize()
        .semantics { isTraversalGroup = true }) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    query = text.value,
                    onQueryChange = { text.value = it },
                    onSearch = { expanded.value = false },
                    expanded = expanded.value,
                    onExpandedChange = { expanded.value = it },
                    placeholder = { Text("Hinted search text") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
                )
            },
            expanded = expanded.value,
            onExpandedChange = { expanded.value = it },
        ) {
            if (listAddress == null || listAddress.isEmpty()) {
                Text("List empty or null")
                return@SearchBar
            }

            LazyColumn {
                items(listAddress) {
                    ListItem(
                        headlineContent = { Text(it.title) },
                        supportingContent = { Text(it.detailAddress) },
                        leadingContent = {
                            Icon(
                                Icons.Filled.Star,
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

//            Column(Modifier.verticalScroll(rememberScrollState())) {
//                repeat(4) { idx ->
//                    val resultText = "Suggestion $idx"
//                    ListItem(
//                        headlineContent = { Text(resultText) },
//                        supportingContent = { Text("Additional info") },
//                        leadingContent = {
//                            Icon(
//                                Icons.Filled.Star,
//                                contentDescription = null
//                            )
//                        },
//                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
//                        modifier = Modifier
//                            .clickable {
//                                text.value = resultText
//                                expanded.value = false
//                            }
//                            .fillMaxWidth()
//                            .padding(horizontal = 16.dp, vertical = 4.dp))
//                }
//            }
//            LazyColumn(
//                contentPadding = PaddingValues(
//                    start = 16.dp,
//                    top = 72.dp,
//                    end = 16.dp,
//                    bottom = 16.dp
//                ),
//                verticalArrangement = Arrangement.spacedBy(8.dp),
//                modifier = Modifier.semantics {
//                    traversalIndex = 1f
//                },
//            ) {
//                val list = List(100) { "Text $it" }
//                items(count = list.size) {
//                    Text(
//                        text = list[it],
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 16.dp),
//                    )
//                }
//            }
        }


    }
}

@Composable
fun MarkerMyLocation(position: LatLng) {
    val state = rememberUpdatedMarkerState(position)
    MarkerComposable(
        state = state,
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = Color.Blue,
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
fun rememberUpdatedMarkerState(newPosition: LatLng): MarkerState =
    remember { MarkerState(position = newPosition) }
        .apply { position = newPosition }


@SuppressLint("UnusedBoxWithConstraintsScope")
@Preview
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

            }
        }
    }
}