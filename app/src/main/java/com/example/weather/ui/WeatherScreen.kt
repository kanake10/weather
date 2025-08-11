package com.example.weather.ui

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import com.example.weather.R
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.weather.api.dto.forecast.WeatherModel
import com.example.weather.db.entities.CurrentWeatherModel
import com.example.weather.db.entities.FavoriteCityEntity
import com.example.weather.db.entities.LocationEntity
import com.example.weather.ui.viewmodel.FavoriteCitiesViewModel
import com.example.weather.ui.viewmodel.LocationWeatherViewModel
import com.example.weather.ui.viewmodel.WeatherViewModel
import com.example.weather.utils.buildWeatherIconUrl
import com.example.weather.utils.formatDate
import com.example.weather.utils.formatToHourPeriod
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    weatherViewModel: WeatherViewModel = hiltViewModel(),
    favoritesViewModel: FavoriteCitiesViewModel = hiltViewModel(),
    locationViewModel: LocationWeatherViewModel = hiltViewModel()
) {
    val currentState by weatherViewModel.currentState.collectAsStateWithLifecycle()
    val forecastState by weatherViewModel.forecastState.collectAsStateWithLifecycle()
    val favoriteCities by favoritesViewModel.favoriteCities.collectAsStateWithLifecycle()
    val locationState by locationViewModel.weatherState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val locationPermission = android.Manifest.permission.ACCESS_FINE_LOCATION

    var permissionGranted by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        if (isGranted) {
            locationViewModel.fetchLocationAndWeather()
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, locationPermission) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            permissionGranted = true
            locationViewModel.fetchLocationAndWeather()
        } else {
            permissionLauncher.launch(locationPermission)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name)
                    )
                        },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.weather_background),
                contentDescription = stringResource(R.string.background_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(36.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(
                        value = weatherViewModel.searchQuery.value,
                        onValueChange = weatherViewModel::onSearchQueryChanged,
                        placeholder = {
                            Text(
                                text = stringResource(R.string.search_your_location),
                                maxLines = 1
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.search_icon)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (weatherViewModel.searchQuery.value.isNotBlank()) {
                                    weatherViewModel.searchWeather()
                                    showBottomSheet = true
                                    scope.launch { bottomSheetState.show() }
                                }
                            }
                        )
                    )
                }
                // Location-based weather card
                when {
                    locationState.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.padding(vertical = 16.dp))
                    }
                    locationState.weather != null -> {
                        Text(
                            text = stringResource(R.string.my_location),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                        LocationWeatherCard(
                            weather = locationState.weather!!,
                            onClick = {
                                weatherViewModel.onSearchQueryChanged(locationState.weather!!.name)
                                weatherViewModel.searchWeather()
                                showBottomSheet = true
                                scope.launch { bottomSheetState.show() }
                            }
                        )
                    }
                    locationState.errorMessage != null -> {
                        Text(
                            text = locationState.errorMessage!!,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Favorite destinations list
                if (favoriteCities.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.fav_destinations),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        items(
                            items = favoriteCities,
                            key = { it.id }
                        ) { city ->

                            val swipeToDismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = {
                                    if (it == SwipeToDismissBoxValue.EndToStart) {
                                        favoritesViewModel.deleteFavoriteCity(city)
                                        true // Dismiss the item visually
                                    } else false // Prevent visual dismiss from start to end
                                }
                            )

                            SwipeToDismissBox(
                                state = swipeToDismissState,
                                backgroundContent = {
                                    when (swipeToDismissState.dismissDirection) {
                                        SwipeToDismissBoxValue.EndToStart -> {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = stringResource(R.string.delete_icon),
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(Color.Red)
                                                    .wrapContentSize(Alignment.CenterEnd)
                                                    .padding(end = 16.dp),
                                                tint = MaterialTheme.colorScheme.onErrorContainer
                                            )
                                        }
                                        SwipeToDismissBoxValue.StartToEnd, SwipeToDismissBoxValue.Settled -> {}
                                    }
                                }
                            ) {
                                FavoriteCityCard(
                                    city = city,
                                    onClick = {
                                        weatherViewModel.onSearchQueryChanged(city.name)
                                        weatherViewModel.searchWeather()
                                        showBottomSheet = true
                                        scope.launch { bottomSheetState.show() }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = bottomSheetState,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    when {
                        forecastState.isLoading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        forecastState.errorMessage != null -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = forecastState.errorMessage!!,
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        forecastState.forecast.isNullOrEmpty() -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.no_forecast),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        else -> {
                            val alreadyFav = favoriteCities.any { it.name.equals(currentState.weather?.name, ignoreCase = true) }

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (currentState.weather?.name!!.isNotEmpty() && !alreadyFav) {
                                        IconButton(onClick = {
                                            currentState.weather?.let { weather ->
                                                val favoriteCity = FavoriteCityEntity(
                                                    id = weather.id,
                                                    name = weather.name,
                                                    main = weather.main,
                                                    wind = weather.wind,
                                                    weather = weather.weather,
                                                    lastUpdated = System.currentTimeMillis()
                                                )
                                                favoritesViewModel.addCityToFavorites(favoriteCity)
                                            }
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = stringResource(R.string.add_icon)
                                            )
                                        }
                                    }

                                    Spacer(Modifier.weight(1f))

                                    IconButton(onClick = {
                                        scope.launch {
                                            bottomSheetState.hide()
                                            showBottomSheet = false
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = stringResource(R.string.close_icon)
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CurrentConditionsCard(
                                        weather = currentState.weather
                                    )
                                }

                                forecastState.hourlyForecast?.takeIf { it.isNotEmpty() }?.let { hourly ->
                                    Text(
                                        text = stringResource(R.string.hourly_conditions),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )

                                    LazyRow(
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    ) {
                                        items(hourly) { item ->
                                            HourlyForecastItem(item)
                                        }
                                    }
                                }

                                currentState.weather?.let {
                                    Text(
                                        text = stringResource(id = R.string.forecast_title, it.name),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )

                                }
                                forecastState.forecast?.let { forecastList ->
                                    LazyColumn(
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(forecastList) { forecastItem ->
                                            ForecastItemView(forecastItem = forecastItem)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ForecastItemView(
    forecastItem: WeatherModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = formatDate(forecastItem.dt_txt),
                    fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp)
                )
                Text(
                    text = "${forecastItem.main.temp} °C"
                )
                Spacer(modifier = Modifier.height(4.dp))
                forecastItem.weather.firstOrNull()?.let {
                    Text(
                        text = it.description,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                AsyncImage(
                    model = buildWeatherIconUrl(forecastItem.weather.firstOrNull()?.icon),
                    contentDescription = stringResource(R.string.forecast_icon),
                    modifier = Modifier
                        .size(64.dp)
                )
            }
        }
    }
}

@Composable
fun FavoriteCityCard(
    city: FavoriteCityEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = buildWeatherIconUrl(city.weather.firstOrNull()?.icon),
                contentDescription = stringResource(R.string.fav_icon),
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(text = city.name, fontWeight = FontWeight.Bold)
                Text(
                    text = city.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercaseChar() } ?: "",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${city.main.temp.toInt()}°C",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun HourlyForecastItem(item: WeatherModel) {
    Column(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = formatToHourPeriod(item.dt_txt),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${item.main.temp}°C",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(4.dp))
        item.weather.firstOrNull()?.main?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}


@Composable
fun CurrentConditionsCard(weather: CurrentWeatherModel?) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .wrapContentWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        weather?.let {
            Text(
                text = it.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        weather?.let {
            Text(
                text = "${weather.main.temp}°C",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        weather!!.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}


@Composable
fun LocationWeatherCard(
    weather: LocationEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = weather.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = weather.weather.firstOrNull()?.description.orEmpty()
                            .replaceFirstChar { it.uppercaseChar() },
                        fontSize = 14.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${weather.main.temp} °C",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }
            }
            AsyncImage(
                model = buildWeatherIconUrl(weather.weather.firstOrNull()?.icon),
                contentDescription = stringResource(R.string.location_icon),
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.Center)
            )
        }
    }
}