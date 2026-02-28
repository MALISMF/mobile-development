package com.example.fragmentdemo26

/**
 * Модель данных погоды для одного дня.
 * dayIndex: 0..6 (Пн..Вс), conditionKey: "clear","cloudy","rain","snow".
 */
data class WeatherDay(
    val dayIndex: Int,
    val tempMin: Int,
    val tempMax: Int,
    val conditionKey: String,
    val humidity: Int,
    val windSpeed: Int,
    val pressure: Int
)

/**
 * Статические данные погоды по городам.
 */
object WeatherData {
    private val moscow = listOf(
        WeatherDay(0, -2, 3, "clear", 65, 5, 755),
        WeatherDay(1, -1, 4, "cloudy", 72, 8, 752),
        WeatherDay(2, 0, 5, "rain", 85, 12, 748),
        WeatherDay(3, -3, 2, "snow", 90, 15, 742),
        WeatherDay(4, -5, 0, "clear", 60, 3, 758),
        WeatherDay(5, -2, 4, "cloudy", 68, 6, 754),
        WeatherDay(6, 1, 6, "rain", 78, 10, 750)
    )
    private val london = listOf(
        WeatherDay(0, 3, 8, "cloudy", 80, 15, 748),
        WeatherDay(1, 4, 9, "rain", 88, 18, 745),
        WeatherDay(2, 5, 10, "rain", 85, 12, 750),
        WeatherDay(3, 4, 9, "cloudy", 78, 10, 752),
        WeatherDay(4, 6, 11, "clear", 65, 8, 755),
        WeatherDay(5, 5, 10, "cloudy", 72, 11, 751),
        WeatherDay(6, 4, 9, "rain", 82, 14, 747)
    )
    private val berlin = listOf(
        WeatherDay(0, -1, 4, "clear", 62, 6, 756),
        WeatherDay(1, 0, 5, "cloudy", 70, 9, 753),
        WeatherDay(2, 1, 6, "rain", 82, 13, 749),
        WeatherDay(3, -2, 3, "snow", 88, 16, 744),
        WeatherDay(4, 0, 5, "cloudy", 68, 7, 754),
        WeatherDay(5, 2, 7, "clear", 58, 5, 758),
        WeatherDay(6, 1, 6, "cloudy", 75, 10, 751)
    )

    val cityKeys = listOf("moscow", "london", "berlin")

    fun getWeatherForCity(cityIndex: Int): List<WeatherDay> = when (cityIndex) {
        0 -> moscow
        1 -> london
        2 -> berlin
        else -> moscow
    }
}
