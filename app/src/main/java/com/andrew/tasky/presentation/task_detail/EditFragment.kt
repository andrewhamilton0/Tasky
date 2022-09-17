package com.andrew.tasky.presentation.task_detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.andrew.tasky.R
import com.andrew.tasky.databinding.FragmentEditBinding

class EditFragment : Fragment(R.layout.fragment_edit) {

    private lateinit var viewModel: EditViewModel
    private lateinit var binding: FragmentEditBinding
    private lateinit var navController: NavController

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditBinding.bind(view)
        navController = Navigation.findNavController(view)

        binding.backButton.setOnClickListener() {
            navController.popBackStack()
        }
    }
}