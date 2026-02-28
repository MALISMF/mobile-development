package com.example.fragmentdemo26

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Элемент списка: город + текущая погода (или загрузка).
 */
data class CityWeatherItem(
    val cityName: String,
    var weather: CurrentWeather?
)

class CityWeatherAdapter(
    private var items: MutableList<CityWeatherItem>,
    private val windUnits: String,
    private val humidityLabel: String,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<CityWeatherAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_city_weather, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.cityName.text = item.cityName
        holder.deleteButton.setOnClickListener { onDeleteClick(position) }
        val w = item.weather
        if (w != null) {
            holder.loading.visibility = View.GONE
            holder.temp.text = "${w.temp.toInt()}°C"
            holder.humidity.text = "$humidityLabel ${w.humidity}%"
            holder.windIcon.setImageResource(R.drawable.ic_wind)
            holder.windText.text = "${(w.windSpeed * 3.6).toInt()} $windUnits"
            holder.cloudsIcon.setImageResource(R.drawable.ic_weather_cloudy)
            holder.cloudsText.text = "${w.cloudsPercent}%"
        } else {
            holder.loading.visibility = View.VISIBLE
            holder.temp.text = ""
            holder.humidity.text = ""
            holder.windText.text = ""
            holder.cloudsText.text = ""
        }
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<CityWeatherItem>) {
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }

    fun updateWeatherAt(position: Int, weather: CurrentWeather?) {
        if (position in items.indices) {
            items[position] = items[position].copy(weather = weather)
            notifyItemChanged(position)
        }
    }

    fun appendCity(cityName: String) {
        items.add(CityWeatherItem(cityName, null))
        notifyItemInserted(items.size - 1)
    }

    fun removeCityAt(position: Int) {
        if (position in items.indices) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cityName: TextView = itemView.findViewById(R.id.item_city_name)
        val deleteButton: View = itemView.findViewById(R.id.item_delete)
        val temp: TextView = itemView.findViewById(R.id.item_temp)
        val humidity: TextView = itemView.findViewById(R.id.item_humidity)
        val windIcon: ImageView = itemView.findViewById(R.id.item_wind_icon)
        val windText: TextView = itemView.findViewById(R.id.item_wind_text)
        val cloudsIcon: ImageView = itemView.findViewById(R.id.item_clouds_icon)
        val cloudsText: TextView = itemView.findViewById(R.id.item_clouds_text)
        val loading: ProgressBar = itemView.findViewById(R.id.item_loading)
    }
}
