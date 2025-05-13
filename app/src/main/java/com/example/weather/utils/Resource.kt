package com.example.weather.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
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

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

