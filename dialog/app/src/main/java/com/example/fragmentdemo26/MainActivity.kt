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

        btnSettings.setOnClickListener {
            SettingsDialogFragment().show(supportFragmentManager, "settings")
        }

        btnBrief.setOnClickListener { showFragment(true) }
        btnDetail.setOnClickListener { showFragment(false) }

        showFragment(AppSettings.isBriefView)
    }

    override fun onSettingsApplied() {
        window?.decorView?.post { recreate() }
    }

    private fun showFragment(brief: Boolean) {
        val fragment: Fragment = if (brief) WeatherBriefFragment() else WeatherDetailFragment()
        fm.beginTransaction()
            .replace(R.id.container_fragm, fragment)
            .commit()
    }
}
