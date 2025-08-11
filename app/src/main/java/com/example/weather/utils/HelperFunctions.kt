package com.example.weather.utils

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject


private const val WEATHER_ICON_BASE_URL = "https://openweathermap.org/img/wn/"
private const val WEATHER_ICON_SUFFIX = "@2x.png"
private const val DEFAULT_ICON_CODE = "01d"
suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    call: suspend () -> T
): Resource<T> {
    return withContext(dispatcher) {
        try {
            Resource.Success(call())
        } catch (e: IOException) {
            Resource.Error("Couldn't reach server. Check your connection.")
        } catch (e: HttpException) {
            val errorMessage = try {
                val errorJson = e.response()?.errorBody()?.string()
                val message = JSONObject(errorJson).optString("message", "Something went wrong")
                message.replaceFirstChar { it.uppercase() }
            } catch (parseException: Exception) {
                "Something went wrong: ${e.code()}"
            }
            Resource.Error(errorMessage)
        } catch (e: Exception) {
            Resource.Error("Unexpected error: ${e.localizedMessage ?: "Unknown error"}")
        }
    }
}


class NetworkHelper @Inject constructor(
    private val context: Context
) {
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnected == true
    }
}


fun formatToHourPeriod(datetime: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("h a", Locale.getDefault())
        val trimmed = datetime.substring(0, 16)
        val date = inputFormat.parse(trimmed)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        "N/A"
    }
}

fun formatDate(dtTxt: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        val date = inputFormat.parse(dtTxt)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dtTxt
    }
}

fun buildWeatherIconUrl(iconCode: String?): String {
    val safeCode = iconCode ?: DEFAULT_ICON_CODE
    return "$WEATHER_ICON_BASE_URL$safeCode$WEATHER_ICON_SUFFIX"
}