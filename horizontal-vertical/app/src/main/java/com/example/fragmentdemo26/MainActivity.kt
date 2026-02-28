package com.example.fragmentdemo26

import android.content.Context
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity(), SettingsDialogFragment.Listener {

    private lateinit var fm: FragmentManager
    private lateinit var btnBrief: Button
    private lateinit var btnDetail: Button
    private lateinit var btnSettings: Button
    private lateinit var btnCities: Button

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(AppSettings.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fm = supportFragmentManager
        btnBrief = findViewById(R.id.btn_brief)
        btnDetail = findViewById(R.id.btn_detail)
        btnSettings = findViewById(R.id.btn_settings)
        btnCities = findViewById(R.id.btn_cities)

        btnSettings.setOnClickListener {
            SettingsDialogFragment().show(supportFragmentManager, "settings")
        }

        btnBrief.setOnClickListener { showFragment(0) }
        btnDetail.setOnClickListener { showFragment(1) }
        btnCities.setOnClickListener { showFragment(2) }

        showFragment(if (AppSettings.isBriefView) 0 else 1)
    }

    override fun onSettingsApplied() {
        window?.decorView?.post { recreate() }
    }

    private fun showFragment(kind: Int) {
        val fragment: Fragment = when (kind) {
            1 -> WeatherDetailFragment()
            2 -> CitiesWeatherFragment()
            else -> WeatherBriefFragment()
        }
        fm.beginTransaction()
            .replace(R.id.container_fragm, fragment)
            .commit()
    }
}
