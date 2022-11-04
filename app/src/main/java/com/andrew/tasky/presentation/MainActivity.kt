package com.andrew.tasky.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.andrew.tasky.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Tasky)
        setContentView(R.layout.activity_main)
    }
}
