package com.andrew.tasky.auth.presentation.screens.register

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.andrew.tasky.R
import com.andrew.tasky.agenda.util.collectLatestLifecycleFlow
import com.andrew.tasky.auth.data.AuthResult
import com.andrew.tasky.auth.util.PasswordValidator
import com.andrew.tasky.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)
        navController = Navigation.findNavController(view)

        binding.apply {

            backButton.setOnClickListener() {
                navController.popBackStack()
            }

            nameEditText.addTextChangedListener {
                viewModel.setIsNameValid(name = it.toString())
            }

            getStartedButton.setOnClickListener {
                if (!viewModel.isPasswordValid(passwordTextField.getText())) {
                    Toast.makeText(
                        context,
                        String.format(
                            resources.getString(R.string.error_invalid_password_format),
                            PasswordValidator.MIN_PASSWORD_LENGTH
                        ),
                        Toast.LENGTH_LONG
                    ).show()
                }
                viewModel.register(
                    name = nameEditText.text.toString(),
                    email = emailTextField.getText(),
                    password = passwordTextField.getText()
                )
            }

            collectLatestLifecycleFlow(viewModel.isNameValid) { isNameValid ->
                nameCheckBox.isVisible = isNameValid
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.authResults.collect { result ->
                when (result) {
                    is AuthResult.Authorized -> {
                        navController.navigate(
                            RegisterFragmentDirections.actionRegisterFragmentToAgendaFragment(),
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
