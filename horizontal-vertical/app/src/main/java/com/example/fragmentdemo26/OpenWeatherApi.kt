package com.example.fragmentdemo26

import com.google.gson.annotations.SerializedName

/**
 * Модель ответа OpenWeatherMap API (data/2.5/weather).
 * По наработкам CurrentWeatherDataBinding — запрос по URL, парсинг JSON через Gson.
 */
data class OpenWeatherResponse(
    val name: String,
    val main: Main,
    val wind: Wind,
    val clouds: Clouds
) {
    data class Main(
        val temp: Double,
        val humidity: Int
    )
    data class Wind(
        val speed: Double,
        val deg: Int? = null
    )
    data class Clouds(
        @SerializedName("all") val all: Int
    )
}

/** Текущая погода для отображения в списке городов. */
data class CurrentWeather(
    val cityName: String,
    val temp: Double,
    val humidity: Int,
    val windSpeed: Double,
    val windDeg: Int?,
    val cloudsPercent: Int
)
