package com.example.fragmentdemo26

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity() {
    lateinit var fm: FragmentManager
    lateinit var frBrief: Fragment
    lateinit var frDetail: Fragment
    lateinit var btnBrief: Button
    lateinit var btnDetail: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fm = supportFragmentManager
        frBrief = WeatherBriefFragment()
        frDetail = WeatherDetailFragment()

        val fr = fm.findFragmentById(R.id.container_fragm)
        if (fr == null) {
            fm.beginTransaction().add(R.id.container_fragm, frBrief).commit()
        }

        btnBrief = findViewById(R.id.btn_brief)
        btnDetail = findViewById(R.id.btn_detail)

        btnBrief.setOnClickListener {
            fm.beginTransaction()
                .replace(R.id.container_fragm, frBrief)
                .commit()
        }

        btnDetail.setOnClickListener {
            fm.beginTransaction()
                .replace(R.id.container_fragm, frDetail)
                .commit()
        }
    }
}
