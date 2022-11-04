package com.andrew.tasky.presentation.register

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.andrew.tasky.R
import com.andrew.tasky.databinding.FragmentRegisterBinding
import com.andrew.tasky.domain.NameValidator
import com.andrew.tasky.util.collectLatestLifecycleFlow

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var viewModel: RegisterViewModel
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)
        navController = Navigation.findNavController(view)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        binding.backButton.setOnClickListener() {
            navController.popBackStack()
        }

        binding.nameEditText.addTextChangedListener {
            if (it != null) {
                viewModel.setIsNameValid(NameValidator().validate(it.toString()))
            }
        }

        binding.getStartedButton.setOnClickListener {
            binding.passwordTextField.isPasswordValid()
        }

        collectLatestLifecycleFlow(viewModel.isNameValid) { isNameValid ->
            binding.nameCheckBox.isVisible = isNameValid
        }
    }
}
