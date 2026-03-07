package com.example.weathertabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class WeatherPagerAdapter(
    activity: FragmentActivity,
    private val cities: List<WeatherInfo>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = cities.size

    override fun createFragment(position: Int): Fragment =
        WeatherFragment.newInstance(position)
}
