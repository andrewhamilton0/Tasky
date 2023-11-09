package com.andrew.tasky.auth.presentation.screens.login

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.andrew.tasky.R
import com.andrew.tasky.agenda.util.collectLatestLifecycleFlow
import com.andrew.tasky.core.data.Resource
import com.andrew.tasky.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var navController: NavController
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding
    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        Unit
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        navController = Navigation.findNavController(view)

        binding.apply {
            signUpButton.setOnClickListener() {
                navController.navigate(
                    LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                )
            }
            loginButton.setOnClickListener() {
                viewModel.login(
                    username = emailTextField.getText(),
                    password = passwordTextField.getText()
                )
            }
        }

        collectLatestLifecycleFlow(viewModel.loginResults) { results ->
            when (results) {
                is Resource.Error -> {
                    Toast.makeText(
                        context,
                        results.message?.asString(requireContext()),
                        Toast.LENGTH_LONG
                    ).show()
                }
                is Resource.Success -> {
                    checkAndPopupNotificationPermission()
                    navController.navigate(
                        LoginFragmentDirections.actionLoginFragmentToAgendaFragment()
                    )
                }
            }
        }
    }

    private fun checkAndPopupNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !areNotificationPermissionsEnabled(requireContext())
        ) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun areNotificationPermissionsEnabled(context: Context): Boolean {
        return when (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
        ) {
            PackageManager.PERMISSION_GRANTED -> true
            else -> false
        }
    }
}
