package com.example.fragmentdemo26

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.Scanner

/**
 * Загрузка текущей погоды по образцу CurrentWeatherDataBinding:
 * URL API OpenWeatherMap, InputStream, Scanner для чтения строки JSON, Gson для парсинга.
 */
object WeatherLoader {

    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/weather"
    private val gson = Gson()

    suspend fun loadWeather(cityName: String, apiKey: String): CurrentWeather? = withContext(Dispatchers.IO) {
        try {
            val encodedCity = java.net.URLEncoder.encode(cityName, "UTF-8")
            val url = "$BASE_URL?q=$encodedCity&appid=$apiKey&units=metric"
            val connection = URL(url).openConnection()
            connection.connectTimeout = 10_000
            connection.readTimeout = 10_000
            val stream = connection.getInputStream()
            val data = Scanner(stream).useDelimiter("\\A").next()
            stream.close()
            val response = gson.fromJson(data, OpenWeatherResponse::class.java)
            CurrentWeather(
                cityName = response.name,
                temp = response.main.temp,
                humidity = response.main.humidity,
                windSpeed = response.wind.speed,
                windDeg = response.wind.deg,
                cloudsPercent = response.clouds.all
            )
        } catch (e: Exception) {
            Log.e("WeatherLoader", "loadWeather: $cityName", e)
            null
        }
    }
}
