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
 * Краткий вариант отображения погоды — в виде иконок по дням недели.
 */
class WeatherBriefFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_weather_brief, container, false)
        val containerLayout = view.findViewById<LinearLayout>(R.id.weather_brief_container)

        WeatherData.weekWeather.forEach { day ->
            val itemView = inflater.inflate(R.layout.item_weather_brief, containerLayout, false)
            itemView.findViewById<TextView>(R.id.brief_day_name).text = day.dayName
            itemView.findViewById<ImageView>(R.id.brief_weather_icon).setImageResource(iconForCondition(day.condition))
            itemView.findViewById<TextView>(R.id.brief_temp).text = "${day.tempMin}° / ${day.tempMax}°"
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
