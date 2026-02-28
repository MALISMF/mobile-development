package com.example.fragmentdemo26

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Список городов, которые пользователь добавил. Сохраняется в SharedPreferences.
 */
object CitiesRepository {

    private const val PREFS_NAME = "cities_list"
    private const val KEY_CITIES_JSON = "cities"
    private val gson = Gson()
    private val type = object : TypeToken<List<String>>() {}.type

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null) prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getCities(): List<String> {
        val json = prefs?.getString(KEY_CITIES_JSON, "[]") ?: "[]"
        return (gson.fromJson<List<String>>(json, type) ?: emptyList()).distinct()
    }

    fun addCity(cityName: String) {
        if (cityName.isBlank()) return
        val list = getCities().toMutableList()
        if (cityName.trim() in list.map { it.trim() }) return
        list.add(cityName.trim())
        prefs?.edit()?.putString(KEY_CITIES_JSON, gson.toJson(list))?.apply()
    }

    fun removeCity(cityName: String) {
        val list = getCities().toMutableList().apply { remove(cityName) }
        prefs?.edit()?.putString(KEY_CITIES_JSON, gson.toJson(list))?.apply()
    }
}
