package com.example.weather.di

import android.content.Context
import androidx.room.Room
import com.example.weather.BuildConfig.BASE_URL
import com.example.weather.api.OpenWeatherApi
import com.example.weather.db.WeatherDatabase
import com.example.weather.db.dao.CurrentWeatherDao
import com.example.weather.db.dao.FavoriteCityDao
import com.example.weather.db.dao.ForecastWeatherDao
import com.example.weather.db.dao.LocationDao
import com.example.weather.iteractor.FavoriteInteractor
import com.example.weather.iteractor.FavoriteInteractorImpl
import com.example.weather.iteractor.LocationRepository
import com.example.weather.iteractor.LocationRepositoryImpl
import com.example.weather.iteractor.WeatherRepo
import com.example.weather.iteractor.WeatherRepoImpl
import com.example.weather.utils.DB
import com.example.weather.utils.LocationManager
import com.example.weather.utils.NetworkHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenWeatherApi(retrofit: Retrofit): OpenWeatherApi {
        return retrofit.create(OpenWeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherDatabase(
        @ApplicationContext context: Context
    ): WeatherDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            WeatherDatabase::class.java,
            DB
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideCurrentWeatherDao(weatherDatabase: WeatherDatabase): CurrentWeatherDao {
        return weatherDatabase.currentWeatherDao
    }

    @Provides
    @Singleton
    fun provideForecastWeatherDao(weatherDatabase: WeatherDatabase): ForecastWeatherDao {
        return weatherDatabase.forecastWeatherDao
    }

    @Provides
    @Singleton
    fun provideFavoriteCityDaoDao(weatherDatabase: WeatherDatabase): FavoriteCityDao {
        return weatherDatabase.favoriteCityDao
    }
    @Provides
    fun provideLocationDao(weatherDatabase: WeatherDatabase): LocationDao = weatherDatabase.locationDao

    @Provides
    fun provideLocationRepository(
        api: OpenWeatherApi,
        dao: LocationDao
    ): LocationRepository = LocationRepositoryImpl(api, dao)

    @Provides
    @Singleton
    fun provideWeatherRepo(
        openWeatherApi: OpenWeatherApi,
        currentWeatherDao: CurrentWeatherDao,
        forecastWeatherDao: ForecastWeatherDao,
        networkHelper: NetworkHelper
    ): WeatherRepo {
        return WeatherRepoImpl(openWeatherApi, currentWeatherDao, forecastWeatherDao, networkHelper)
    }

    @Provides
    @Singleton
    fun provideFavoritesInteractor(
        favoriteCityDao: FavoriteCityDao
    ): FavoriteInteractor {
        return FavoriteInteractorImpl (favoriteCityDao)
    }

    @Provides
    @Singleton
    fun provideNetworkHelper(@ApplicationContext context: Context): NetworkHelper {
        return NetworkHelper(context)
    }

    @Provides
    @Singleton
    fun provideLocationManager(@ApplicationContext context: Context): LocationManager {
        return LocationManager(context)
    }
}

