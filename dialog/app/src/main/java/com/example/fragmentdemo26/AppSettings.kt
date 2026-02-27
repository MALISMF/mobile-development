package com.example.fragmentdemo26

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

/**
 * Настройки приложения: язык, вид (краткий/детальный), город.
 * Сохраняются в SharedPreferences и применяются к контенту.
 */
object AppSettings {
    private const val PREFS_NAME = "weather_settings"
    private const val KEY_LANGUAGE = "language"      // 0 = ru, 1 = en
    private const val KEY_VIEW_TYPE = "view_type"     // 0 = brief, 1 = detail
    private const val KEY_CITY = "city"              // 0, 1, 2...

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null) prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var languageIndex: Int
        get() = prefs?.getInt(KEY_LANGUAGE, 0) ?: 0
        set(value) { prefs?.edit()?.putInt(KEY_LANGUAGE, value)?.apply() }

    var viewTypeIndex: Int
        get() = prefs?.getInt(KEY_VIEW_TYPE, 0) ?: 0
        set(value) { prefs?.edit()?.putInt(KEY_VIEW_TYPE, value)?.apply() }

    var cityIndex: Int
        get() = prefs?.getInt(KEY_CITY, 0) ?: 0
        set(value) { prefs?.edit()?.putInt(KEY_CITY, value)?.apply() }

    val isRussian: Boolean get() = languageIndex == 0
    val isBriefView: Boolean get() = viewTypeIndex == 0

    /** Конфигурация с выбранной локалью для применения в Activity. */
    fun applyToConfiguration(context: Context): Configuration {
        val lang = if (languageIndex == 0) "ru" else "en"
        val config = Configuration(context.resources.configuration)
        config.setLocale(Locale(lang))
        return config
    }

    /** Контекст с выбранной локалью (для attachBaseContext). */
    fun wrapContext(context: Context): Context {
        init(context)
        val config = Configuration(context.resources.configuration)
        config.setLocale(Locale(if (languageIndex == 0) "ru" else "en"))
        return context.createConfigurationContext(config)
    }
}
