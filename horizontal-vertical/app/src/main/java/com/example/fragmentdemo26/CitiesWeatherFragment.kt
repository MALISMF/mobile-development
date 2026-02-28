package com.example.fragmentdemo26

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Список городов с текущей погодой (название, температура и влажность текстом, ветер и облачность иконками).
 * RecyclerView: ориентация задаётся при создании LayoutManager до назначения адаптера.
 */
class CitiesWeatherFragment : Fragment() {

    private val scope = CoroutineScope(Job() + Dispatchers.Main)
    private var adapter: CityWeatherAdapter? = null
    private val items = mutableListOf<CityWeatherItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        CitiesRepository.init(requireContext())
        val view = inflater.inflate(R.layout.fragment_cities_weather, container, false)
        val recycler = view.findViewById<RecyclerView>(R.id.cities_recycler)

        // Ориентация указывается при создании RecyclerView до назначения адаптера
        val layoutManager = LinearLayoutManager(
            requireContext().applicationContext,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        recycler.layoutManager = layoutManager

        val apiKey = getString(R.string.openweather_api_key)
        val windUnits = getString(R.string.wind_units)
        val humidityLabel = getString(R.string.humidity).replace(":", "").trim()

        items.clear()
        items.addAll(CitiesRepository.getCities().map { CityWeatherItem(it, null) })
        adapter = CityWeatherAdapter(items, windUnits, humidityLabel) { position ->
            if (position in items.indices) {
                val cityName = items[position].cityName
                CitiesRepository.removeCity(cityName)
                adapter?.removeCityAt(position)
            }
        }
        recycler.adapter = adapter

        loadAllWeather(apiKey)

        view.findViewById<View>(R.id.fab_add_city).setOnClickListener {
            val dialog = AddCityDialogFragment().apply {
                onCityAdded = { cityName ->
                    CitiesRepository.addCity(cityName)
                    adapter?.appendCity(cityName)
                    scope.launch {
                        val index = items.size - 1
                        val w = withContext(Dispatchers.IO) {
                            WeatherLoader.loadWeather(cityName, apiKey)
                        }
                        if (index in items.indices) {
                            items[index] = items[index].copy(weather = w)
                            adapter?.updateWeatherAt(index, w)
                        }
                    }
                }
            }
            dialog.show(childFragmentManager, "add_city")
        }

        return view
    }

    private fun loadAllWeather(apiKey: String) {
        items.forEachIndexed { index, item ->
            if (item.weather == null) {
                scope.launch {
                    val w = withContext(Dispatchers.IO) {
                        WeatherLoader.loadWeather(item.cityName, apiKey)
                    }
                    if (index in items.indices) {
                        items[index] = items[index].copy(weather = w)
                        adapter?.updateWeatherAt(index, w)
                    }
                }
            }
        }
    }
}
