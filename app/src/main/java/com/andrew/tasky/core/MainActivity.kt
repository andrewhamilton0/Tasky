package com.andrew.tasky.core

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.andrew.tasky.R
import com.andrew.tasky.agenda.util.collectLatestLifecycleFlow
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Tasky)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainActivityFragment)
            as NavHostFragment
        navController = navHostFragment.navController

        collectLatestLifecycleFlow(viewModel.userIsInitiallyLoggedIn) {
            navController.navigate(R.id.action_global_agendaFragment)
        }
    }
}
