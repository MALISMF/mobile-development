package com.example.weathertabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WeatherFragment : Fragment() {

    companion object {
        private const val ARG_CITY_INDEX = "city_index"

        fun newInstance(cityIndex: Int) = WeatherFragment().apply {
            arguments = Bundle().apply { putInt(ARG_CITY_INDEX, cityIndex) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_weather, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val index = arguments?.getInt(ARG_CITY_INDEX) ?: 0
        val weather = CityWeatherRepository.getCities()[index]

        // Background
        view.findViewById<View>(R.id.weatherRoot)
            .setBackgroundResource(weather.backgroundRes)

        // Main info
        view.findViewById<TextView>(R.id.tvCityName).text = weather.cityName
        view.findViewById<TextView>(R.id.tvCountry).text = weather.countryCode
        view.findViewById<ImageView>(R.id.ivWeatherIcon)
            .setImageResource(weather.iconRes)
        view.findViewById<TextView>(R.id.tvTemp).text =
            formatTemp(weather.tempC)
        view.findViewById<TextView>(R.id.tvDescription).text = weather.description
        view.findViewById<TextView>(R.id.tvFeelsLike).text =
            "Ощущается как ${formatTemp(weather.feelsLike)}"

        // Details
        view.findViewById<TextView>(R.id.tvHumidity).text = "${weather.humidity}%"
        view.findViewById<TextView>(R.id.tvWind).text = "${weather.windSpeed} км/ч"
        view.findViewById<TextView>(R.id.tvPressure).text = "${weather.pressure} мм"
        view.findViewById<TextView>(R.id.tvVisibility).text = "${weather.visibility} км"
        view.findViewById<TextView>(R.id.tvUvIndex).text = "${weather.uvIndex}"
        view.findViewById<TextView>(R.id.tvSunrise).text = weather.sunrise
        view.findViewById<TextView>(R.id.tvSunset).text = weather.sunset

        // Hourly forecast
        val rvHourly = view.findViewById<RecyclerView>(R.id.rvHourly)
        rvHourly.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvHourly.adapter = HourlyAdapter(weather.hourly)

        // Weekly forecast
        val rvWeekly = view.findViewById<RecyclerView>(R.id.rvWeekly)
        rvWeekly.layoutManager = LinearLayoutManager(requireContext())
        rvWeekly.isNestedScrollingEnabled = false
        rvWeekly.adapter = WeeklyAdapter(weather.weekly)
    }

    private fun formatTemp(t: Int) = if (t > 0) "+$t°" else "$t°"
}
