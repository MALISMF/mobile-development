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
 * Детализированный вариант отображения погоды — полная информация по каждому дню.
 */
class WeatherDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_weather_detail, container, false)
        val containerLayout = view.findViewById<LinearLayout>(R.id.weather_detail_container)

        WeatherData.weekWeather.forEach { day ->
            val itemView = inflater.inflate(R.layout.item_weather_detail, containerLayout, false)
            itemView.findViewById<ImageView>(R.id.detail_weather_icon).setImageResource(iconForCondition(day.condition))
            itemView.findViewById<TextView>(R.id.detail_day_name).text = day.dayName
            itemView.findViewById<TextView>(R.id.detail_condition).text = day.condition
            itemView.findViewById<TextView>(R.id.detail_temp).text = "${day.tempMin}° … ${day.tempMax}°"
            itemView.findViewById<TextView>(R.id.detail_humidity).text = "${day.humidity}%"
            itemView.findViewById<TextView>(R.id.detail_wind).text = "${day.windSpeed} км/ч"
            itemView.findViewById<TextView>(R.id.detail_pressure).text = "${day.pressure} мм"
            containerLayout.addView(itemView)
        }

        return view
    }

    private fun iconForCondition(condition: String): Int = when (condition.lowercase()) {
        "ясно" -> R.drawable.ic_weather_sunny
        "облачно" -> R.drawable.ic_weather_cloudy
        "дождь" -> R.drawable.ic_weather_rain
        "снег" -> R.drawable.ic_weather_snow
        else -> R.drawable.ic_weather_cloudy
    }
}
