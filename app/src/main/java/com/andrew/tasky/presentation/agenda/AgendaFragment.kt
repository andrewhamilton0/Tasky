package com.andrew.tasky.presentation.agenda

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.andrew.tasky.R
import com.andrew.tasky.databinding.FragmentAgendaBinding
import com.andrew.tasky.util.TaskType

class AgendaFragment : Fragment(R.layout.fragment_agenda) {

    private lateinit var navController: NavController
    private lateinit var viewModel: AgendaViewModel
    private lateinit var binding: FragmentAgendaBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        viewModel = ViewModelProvider(this).get(AgendaViewModel::class.java)
        binding = FragmentAgendaBinding.bind(view)

        binding.addTask.setOnClickListener(){
            val taskType = TaskType.TASK.ordinal
            val bundle = bundleOf("taskType" to taskType)
            navController.navigate(R.id.action_agendaFragment_to_taskDetailFragment, bundle)
        }

        binding.addEvent.setOnClickListener(){
            val taskType = TaskType.EVENT.ordinal
            val bundle = bundleOf("taskType" to taskType)
            navController.navigate(R.id.action_agendaFragment_to_taskDetailFragment, bundle)
        }

        binding.addReminder.setOnClickListener(){
            val taskType = TaskType.REMINDER.ordinal
            val bundle = bundleOf("taskType" to taskType)
            navController.navigate(R.id.action_agendaFragment_to_taskDetailFragment, bundle)
        }
    }
}