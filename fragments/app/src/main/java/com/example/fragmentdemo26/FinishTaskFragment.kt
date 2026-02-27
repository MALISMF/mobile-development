package com.example.fragmentdemo26

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class FinishTaskFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_finish_task, container, false)
        view.setBackgroundColor(Color.YELLOW)
        return view
        //return super.onCreateView(inflater, container, savedInstanceState)
    }
}