package com.andrew.tasky.auth.presentation.screens.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.andrew.tasky.R
import com.andrew.tasky.auth.data.AuthResult
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

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.authResults.collect { result ->
                when (result) {
                    is AuthResult.Authorized -> {
                        navController.navigate(
                            LoginFragmentDirections.actionLoginFragmentToAgendaFragment(),
                            NavOptions.Builder().setPopUpTo(R.id.agendaFragment, inclusive = true)
                                .build()
                        )
                    }
                    is AuthResult.Unauthorized -> Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.unauthorized),
                        Toast.LENGTH_LONG
                    ).show()
                    is AuthResult.UnknownError -> Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.unknown_error),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
