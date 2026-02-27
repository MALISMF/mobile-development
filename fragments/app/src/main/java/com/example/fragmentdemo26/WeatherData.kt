package com.example.fragmentdemo26

/**
 * Модель данных погоды для одного дня.
 * Данные статические, задаются в переменных.
 */
data class WeatherDay(
    val dayName: String,
    val tempMin: Int,
    val tempMax: Int,
    val condition: String,      // "ясно", "облачно", "дождь", "снег"
    val humidity: Int,          // влажность %
    val windSpeed: Int,         // скорость ветра км/ч
    val pressure: Int           // давление мм рт.ст.
)

/**
 * Статические данные погоды на неделю.
 */
object WeatherData {
    val weekWeather: List<WeatherDay> = listOf(
        WeatherDay("Пн", -2, 3, "ясно", 65, 5, 755),
        WeatherDay("Вт", -1, 4, "облачно", 72, 8, 752),
        WeatherDay("Ср", 0, 5, "дождь", 85, 12, 748),
        WeatherDay("Чт", -3, 2, "снег", 90, 15, 742),
        WeatherDay("Пт", -5, 0, "ясно", 60, 3, 758),
        WeatherDay("Сб", -2, 4, "облачно", 68, 6, 754),
        WeatherDay("Вс", 1, 6, "дождь", 78, 10, 750)
    )
}
