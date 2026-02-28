package com.example.fragmentdemo26

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

/**
 * Детализированный вариант отображения погоды.
 * Город и язык берутся из AppSettings.
 */
class WeatherDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppSettings.init(requireContext())
        val view = inflater.inflate(R.layout.fragment_weather_detail, container, false)
        val containerLayout = view.findViewById<LinearLayout>(R.id.weather_detail_container)
        val dayNames = resources.getStringArray(R.array.day_names_short)
        val weather = WeatherData.getWeatherForCity(AppSettings.cityIndex)

        weather.forEach { day ->
            val itemView = inflater.inflate(R.layout.item_weather_detail, containerLayout, false)
            itemView.findViewById<ImageView>(R.id.detail_weather_icon).setImageResource(iconForCondition(day.conditionKey))
            itemView.findViewById<TextView>(R.id.detail_day_name).text = dayNames.getOrElse(day.dayIndex) { "" }
            itemView.findViewById<TextView>(R.id.detail_condition).text = getConditionText(day.conditionKey)
            itemView.findViewById<TextView>(R.id.detail_temp).text = "${day.tempMin}° … ${day.tempMax}°"
            itemView.findViewById<TextView>(R.id.detail_humidity).text = "${day.humidity}%"
            itemView.findViewById<TextView>(R.id.detail_wind).text = "${day.windSpeed} ${getString(R.string.wind_units)}"
            itemView.findViewById<TextView>(R.id.detail_pressure).text = "${day.pressure} ${getString(R.string.pressure_units)}"
            containerLayout.addView(itemView)
        }

        return view
    }

    private fun getConditionText(conditionKey: String): String = getString(
        when (conditionKey) {
            "clear" -> R.string.condition_clear
            "cloudy" -> R.string.condition_cloudy
            "rain" -> R.string.condition_rain
            "snow" -> R.string.condition_snow
            else -> R.string.condition_cloudy
        }
    )

    private fun iconForCondition(conditionKey: String): Int = when (conditionKey) {
        "clear" -> R.drawable.ic_weather_sunny
        "cloudy" -> R.drawable.ic_weather_cloudy
        "rain" -> R.drawable.ic_weather_rain
        "snow" -> R.drawable.ic_weather_snow
        else -> R.drawable.ic_weather_cloudy
    }
}
