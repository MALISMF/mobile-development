package com.example.weathertabs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HourlyAdapter(private val items: List<HourlyForecast>) :
    RecyclerView.Adapter<HourlyAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvTime: TextView = view.findViewById(R.id.tvHourTime)
        val ivIcon: ImageView = view.findViewById(R.id.ivHourIcon)
        val tvTemp: TextView = view.findViewById(R.id.tvHourTemp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hourly, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tvTime.text = item.time
        holder.ivIcon.setImageResource(item.iconRes)
        holder.tvTemp.text = formatTemp(item.tempC)
    }

    override fun getItemCount() = items.size

    private fun formatTemp(t: Int) = if (t > 0) "+$t°" else "$t°"
}
