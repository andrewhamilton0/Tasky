package com.andrew.tasky.presentation.task_detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.andrew.tasky.R
import com.andrew.tasky.databinding.FragmentTaskDetailBinding

class TaskDetailFragment : Fragment(R.layout.fragment_task_detail) {

    private lateinit var viewModel: TaskDetailViewModel
    private lateinit var navController: NavController
    private lateinit var binding: FragmentTaskDetailBinding

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TaskDetailViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTaskDetailBinding.bind(view)
        navController = Navigation.findNavController(view)

        binding.editDescriptionButton.setOnClickListener(){
            navController.navigate(R.id.action_taskDetailFragment_to_editFragment)
        }
    }
}