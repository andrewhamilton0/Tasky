package com.andrew.tasky.presentation.register

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.andrew.tasky.R
import com.andrew.tasky.databinding.FragmentRegisterBinding
import java.util.regex.Pattern

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var viewModel: RegisterViewModel
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var navController: NavController

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)
        navController = Navigation.findNavController(view)

        binding.backButton.setOnClickListener(){
            navController.popBackStack()
        }

        binding.nameEditText.addTextChangedListener{
            if (it != null) {
                binding.nameCheckBox.isVisible = it.length in 2..50
            }
        }
        binding.getStartedButton.setOnClickListener {
            binding.passwordTextField.isPasswordValid()
        }
    }
}