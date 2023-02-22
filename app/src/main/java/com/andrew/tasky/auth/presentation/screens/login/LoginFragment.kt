package com.andrew.tasky.auth.presentation.screens.login

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.andrew.tasky.R
import com.andrew.tasky.agenda.util.collectLatestLifecycleFlow
import com.andrew.tasky.core.Resource
import com.andrew.tasky.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var navController: NavController
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding

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
                    Toast.makeText(context, results.message, Toast.LENGTH_LONG).show()
                }
                is Resource.Success -> {
                    navController.navigate(
                        LoginFragmentDirections.actionLoginFragmentToAgendaFragment()
                    )
                }
            }
        }
        collectLatestLifecycleFlow(viewModel.authenticateResults) { results ->
            when (results) {
                is Resource.Error -> {
                    Log.e("LoginFragAuthResult", results.message ?: "Unknown error")
                }
                is Resource.Success -> {
                    navController.navigate(
                        LoginFragmentDirections.actionLoginFragmentToAgendaFragment()
                    )
                }
            }
        }
    }
}
