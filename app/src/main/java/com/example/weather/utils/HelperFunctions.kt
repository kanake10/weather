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
import javax.inject.Inject

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
