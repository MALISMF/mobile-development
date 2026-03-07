package com.example.weathertabs

data class HourlyForecast(
    val time: String,
    val tempC: Int,
    val iconRes: Int,
    val description: String
)

data class DailyForecast(
    val day: String,
    val tempMin: Int,
    val tempMax: Int,
    val iconRes: Int,
    val description: String
)

data class WeatherInfo(
    val cityName: String,
    val countryCode: String,
    val tempC: Int,
    val feelsLike: Int,
    val description: String,
    val humidity: Int,
    val windSpeed: Int,
    val pressure: Int,
    val visibility: Int,
    val uvIndex: Int,
    val sunrise: String,
    val sunset: String,
    val iconRes: Int,
    val backgroundRes: Int,
    val hourly: List<HourlyForecast>,
    val weekly: List<DailyForecast>
)

object CityWeatherRepository {

    fun getCities(): List<WeatherInfo> = listOf(
        WeatherInfo(
            cityName = "Москва",
            countryCode = "RU",
            tempC = -3,
            feelsLike = -8,
            description = "Облачно с прояснениями",
            humidity = 78,
            windSpeed = 14,
            pressure = 748,
            visibility = 9,
            uvIndex = 1,
            sunrise = "08:42",
            sunset = "17:21",
            iconRes = R.drawable.ic_cloudy,
            backgroundRes = R.drawable.bg_cloudy,
            hourly = listOf(
                HourlyForecast("09:00", -4, R.drawable.ic_cloudy, "Облачно"),
                HourlyForecast("12:00", -2, R.drawable.ic_partly_cloudy, "Переменная"),
                HourlyForecast("15:00", -1, R.drawable.ic_partly_cloudy, "Переменная"),
                HourlyForecast("18:00", -3, R.drawable.ic_cloudy, "Облачно"),
                HourlyForecast("21:00", -5, R.drawable.ic_night_cloudy, "Пасмурно"),
                HourlyForecast("00:00", -6, R.drawable.ic_night_cloudy, "Пасмурно")
            ),
            weekly = listOf(
                DailyForecast("Сегодня", -6, -1, R.drawable.ic_cloudy, "Облачно"),
                DailyForecast("Вт", -7, 0, R.drawable.ic_snow, "Снег"),
                DailyForecast("Ср", -9, -3, R.drawable.ic_snow, "Метель"),
                DailyForecast("Чт", -5, 1, R.drawable.ic_partly_cloudy, "Переменная"),
                DailyForecast("Пт", -2, 3, R.drawable.ic_sunny, "Ясно"),
                DailyForecast("Сб", 0, 4, R.drawable.ic_partly_cloudy, "Переменная"),
                DailyForecast("Вс", -3, 1, R.drawable.ic_cloudy, "Облачно")
            )
        ),
        WeatherInfo(
            cityName = "Санкт-Петербург",
            countryCode = "RU",
            tempC = -5,
            feelsLike = -11,
            description = "Снегопад",
            humidity = 85,
            windSpeed = 18,
            pressure = 742,
            visibility = 4,
            uvIndex = 0,
            sunrise = "09:05",
            sunset = "17:02",
            iconRes = R.drawable.ic_snow,
            backgroundRes = R.drawable.bg_snow,
            hourly = listOf(
                HourlyForecast("09:00", -6, R.drawable.ic_snow, "Снег"),
                HourlyForecast("12:00", -4, R.drawable.ic_snow, "Снег"),
                HourlyForecast("15:00", -4, R.drawable.ic_cloudy, "Облачно"),
                HourlyForecast("18:00", -6, R.drawable.ic_snow, "Снег"),
                HourlyForecast("21:00", -7, R.drawable.ic_snow, "Снег"),
                HourlyForecast("00:00", -8, R.drawable.ic_night_cloudy, "Пасмурно")
            ),
            weekly = listOf(
                DailyForecast("Сегодня", -8, -3, R.drawable.ic_snow, "Снегопад"),
                DailyForecast("Вт", -10, -4, R.drawable.ic_snow, "Метель"),
                DailyForecast("Ср", -8, -2, R.drawable.ic_cloudy, "Пасмурно"),
                DailyForecast("Чт", -6, 0, R.drawable.ic_cloudy, "Облачно"),
                DailyForecast("Пт", -4, 2, R.drawable.ic_partly_cloudy, "Переменная"),
                DailyForecast("Сб", -3, 3, R.drawable.ic_partly_cloudy, "Переменная"),
                DailyForecast("Вс", -5, 0, R.drawable.ic_snow, "Снег")
            )
        ),
        WeatherInfo(
            cityName = "Сочи",
            countryCode = "RU",
            tempC = 12,
            feelsLike = 10,
            description = "Ясно и солнечно",
            humidity = 62,
            windSpeed = 7,
            pressure = 762,
            visibility = 18,
            uvIndex = 4,
            sunrise = "07:28",
            sunset = "18:15",
            iconRes = R.drawable.ic_sunny,
            backgroundRes = R.drawable.bg_sunny,
            hourly = listOf(
                HourlyForecast("09:00", 10, R.drawable.ic_sunny, "Ясно"),
                HourlyForecast("12:00", 14, R.drawable.ic_sunny, "Ясно"),
                HourlyForecast("15:00", 15, R.drawable.ic_sunny, "Солнечно"),
                HourlyForecast("18:00", 13, R.drawable.ic_partly_cloudy, "Переменная"),
                HourlyForecast("21:00", 10, R.drawable.ic_night_clear, "Ясно"),
                HourlyForecast("00:00", 9, R.drawable.ic_night_clear, "Ясно")
            ),
            weekly = listOf(
                DailyForecast("Сегодня", 9, 15, R.drawable.ic_sunny, "Солнечно"),
                DailyForecast("Вт", 10, 16, R.drawable.ic_sunny, "Солнечно"),
                DailyForecast("Ср", 8, 14, R.drawable.ic_partly_cloudy, "Переменная"),
                DailyForecast("Чт", 7, 13, R.drawable.ic_rain, "Дождь"),
                DailyForecast("Пт", 9, 15, R.drawable.ic_partly_cloudy, "Переменная"),
                DailyForecast("Сб", 11, 17, R.drawable.ic_sunny, "Солнечно"),
                DailyForecast("Вс", 12, 18, R.drawable.ic_sunny, "Солнечно")
            )
        ),
        WeatherInfo(
            cityName = "Новосибирск",
            countryCode = "RU",
            tempC = -18,
            feelsLike = -26,
            description = "Морозно, ясно",
            humidity = 70,
            windSpeed = 10,
            pressure = 755,
            visibility = 15,
            uvIndex = 0,
            sunrise = "09:22",
            sunset = "17:48",
            iconRes = R.drawable.ic_frost,
            backgroundRes = R.drawable.bg_frost,
            hourly = listOf(
                HourlyForecast("09:00", -19, R.drawable.ic_frost, "Мороз"),
                HourlyForecast("12:00", -16, R.drawable.ic_sunny, "Ясно"),
                HourlyForecast("15:00", -15, R.drawable.ic_sunny, "Ясно"),
                HourlyForecast("18:00", -18, R.drawable.ic_frost, "Мороз"),
                HourlyForecast("21:00", -21, R.drawable.ic_night_clear, "Ясно"),
                HourlyForecast("00:00", -23, R.drawable.ic_night_clear, "Мороз")
            ),
            weekly = listOf(
                DailyForecast("Сегодня", -23, -14, R.drawable.ic_frost, "Мороз"),
                DailyForecast("Вт", -25, -17, R.drawable.ic_frost, "Сильный мороз"),
                DailyForecast("Ср", -20, -12, R.drawable.ic_sunny, "Ясно"),
                DailyForecast("Чт", -15, -8, R.drawable.ic_partly_cloudy, "Переменная"),
                DailyForecast("Пт", -18, -10, R.drawable.ic_cloudy, "Облачно"),
                DailyForecast("Сб", -22, -14, R.drawable.ic_frost, "Мороз"),
                DailyForecast("Вс", -19, -11, R.drawable.ic_sunny, "Ясно")
            )
        ),
        WeatherInfo(
            cityName = "Владивосток",
            countryCode = "RU",
            tempC = -8,
            feelsLike = -14,
            description = "Переменная облачность",
            humidity = 72,
            windSpeed = 22,
            pressure = 758,
            visibility = 12,
            uvIndex = 2,
            sunrise = "07:15",
            sunset = "17:35",
            iconRes = R.drawable.ic_partly_cloudy,
            backgroundRes = R.drawable.bg_cloudy,
            hourly = listOf(
                HourlyForecast("09:00", -9, R.drawable.ic_partly_cloudy, "Переменная"),
                HourlyForecast("12:00", -6, R.drawable.ic_partly_cloudy, "Переменная"),
                HourlyForecast("15:00", -5, R.drawable.ic_sunny, "Ясно"),
                HourlyForecast("18:00", -8, R.drawable.ic_cloudy, "Облачно"),
                HourlyForecast("21:00", -10, R.drawable.ic_night_cloudy, "Облачно"),
                HourlyForecast("00:00", -11, R.drawable.ic_night_cloudy, "Пасмурно")
            ),
            weekly = listOf(
                DailyForecast("Сегодня", -11, -4, R.drawable.ic_partly_cloudy, "Переменная"),
                DailyForecast("Вт", -9, -2, R.drawable.ic_cloudy, "Облачно"),
                DailyForecast("Ср", -12, -5, R.drawable.ic_snow, "Снег"),
                DailyForecast("Чт", -14, -7, R.drawable.ic_frost, "Мороз"),
                DailyForecast("Пт", -10, -3, R.drawable.ic_partly_cloudy, "Переменная"),
                DailyForecast("Сб", -8, 0, R.drawable.ic_partly_cloudy, "Переменная"),
                DailyForecast("Вс", -6, 2, R.drawable.ic_cloudy, "Облачно")
            )
        )
    )
}
