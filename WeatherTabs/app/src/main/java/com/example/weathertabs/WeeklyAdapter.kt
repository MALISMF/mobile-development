package com.example.weathertabs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WeeklyAdapter(private val items: List<DailyForecast>) :
    RecyclerView.Adapter<WeeklyAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvDay: TextView = view.findViewById(R.id.tvDay)
        val ivIcon: ImageView = view.findViewById(R.id.ivDayIcon)
        val tvDesc: TextView = view.findViewById(R.id.tvDayDesc)
        val tvMin: TextView = view.findViewById(R.id.tvTempMin)
        val tvMax: TextView = view.findViewById(R.id.tvTempMax)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tvDay.text = item.day
        holder.ivIcon.setImageResource(item.iconRes)
        holder.tvDesc.text = item.description
        holder.tvMin.text = formatTemp(item.tempMin)
        holder.tvMax.text = formatTemp(item.tempMax)
    }

    override fun getItemCount() = items.size

    private fun formatTemp(t: Int) = if (t > 0) "+$t°" else "$t°"
}
