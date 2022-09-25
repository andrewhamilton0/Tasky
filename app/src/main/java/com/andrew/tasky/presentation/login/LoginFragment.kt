package com.andrew.tasky.presentation.login

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.andrew.tasky.R
import com.andrew.tasky.databinding.FragmentLoginBinding

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var navController: NavController
    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: FragmentLoginBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        navController = Navigation.findNavController(view)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        binding.signUpButton.setOnClickListener(){
            navController.navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.loginButton.setOnClickListener(){
            navController.navigate(R.id.action_loginFragment_to_agendaFragment)
        }
    }
}