package com.example.weather.ui

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import com.example.weather.R
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.weather.api.dto.forecast.WeatherModel
import com.example.weather.db.entities.CurrentWeatherModel
import com.example.weather.ui.viewmodel.FavoriteCitiesViewModel
import com.example.weather.ui.viewmodel.WeatherViewModel
import com.example.weather.utils.formatDate
import com.example.weather.utils.formatTimestamp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = hiltViewModel(),
    favoritesViewModel: FavoriteCitiesViewModel = hiltViewModel()) {
    val currentState by viewModel.currentState.collectAsStateWithLifecycle()
    val forecastState by viewModel.forecastState.collectAsStateWithLifecycle()
    val favoriteCities by favoritesViewModel.favoriteCities.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather App") },
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
                contentDescription = null,
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
                        value = viewModel.searchQuery.value,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        placeholder = { Text("Search Your City", maxLines = 1) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon"
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
                                if (viewModel.searchQuery.value.isNotBlank()) {
                                    viewModel.searchWeather()
                                }
                            }
                        )
                    )
                }
                if (favoriteCities.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Favorite Cities:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        items(favoriteCities) { city ->
                            AssistChip(
                                onClick = {
                                    viewModel.onSearchQueryChanged(city.name)
                                    viewModel.searchWeather()
                                },
                                label = { Text(city.name) }
                            )
                        }
                    }
                }


                if (currentState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                } else {
                    currentState.weather?.let { weather ->
                        Text(
                            text = "Current Weather",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                        CurrentWeatherCard(
                            weather = weather,
                            onClick = {
                                if (!forecastState.forecast.isNullOrEmpty()) {
                                    showBottomSheet = true
                                    scope.launch {
                                        bottomSheetState.show()
                                    }
                                }
                            }
                        )
                    }

                    currentState.errorMessage?.let {
                        Text(it, color = Color.Red)
                    }
                }
            }

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = bottomSheetState,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    forecastState.forecast?.let { forecastList ->
                        val cityName = currentState.weather?.name ?: ""
                        val alreadyFavorite = favoriteCities.any { it.name.equals(cityName, ignoreCase = true) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$cityName 5-Day Forecast",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            if (cityName.isNotEmpty() && !alreadyFavorite) {
                                IconButton(
                                    onClick = {
                                        favoritesViewModel.addFavoriteCity(cityName)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add city",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(forecastList) { forecastItem ->
                                ForecastItemView(
                                    forecastItem = forecastItem,
                                    cityName = cityName,
                                )
                            }
                        }
                    } ?: run {
                        Text(
                            text = "No forecast data available.",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun CurrentWeatherCard(weather: CurrentWeatherModel, onClick: () -> Unit) {
    val iconCode = weather.weather?.firstOrNull()?.icon
    val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
    val lastUpdatedFormatted = formatTimestamp(weather.lastUpdated)

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
                        text = weather.name ?: "",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = weather.weather?.firstOrNull()?.description.orEmpty()
                            .replaceFirstChar { it.uppercaseChar() },
                        fontSize = 14.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = lastUpdatedFormatted,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${weather.main.temp} °C",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }
            }

            iconCode?.let {
                Image(
                    painter = rememberAsyncImagePainter(iconUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
}


@Composable
fun ForecastItemView(
    forecastItem: WeatherModel,
    cityName: String
) {
    val formattedDate = formatDate(forecastItem.dt_txt)
    val iconCode = forecastItem.weather.firstOrNull()?.icon
    val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"

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
                Text(text = formattedDate, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${forecastItem.main.temp} °C")
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
                iconCode?.let {
                    Image(
                        painter = rememberAsyncImagePainter(iconUrl),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
        }
    }
}