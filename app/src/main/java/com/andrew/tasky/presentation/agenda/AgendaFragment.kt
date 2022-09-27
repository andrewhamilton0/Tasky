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
import com.andrew.tasky.util.AgendaItemType

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
            val agendaItemType = AgendaItemType.TASK.name
            navController.navigate(AgendaFragmentDirections
                .actionAgendaFragmentToAgendaItemDetailFragment(agendaItemType))
        }

        binding.addEvent.setOnClickListener(){
            val agendaItemType = AgendaItemType.EVENT.name
            navController.navigate(AgendaFragmentDirections
                .actionAgendaFragmentToAgendaItemDetailFragment(agendaItemType))
        }

        binding.addReminder.setOnClickListener(){
            val agendaItemType = AgendaItemType.REMINDER.name
            navController.navigate(AgendaFragmentDirections
                .actionAgendaFragmentToAgendaItemDetailFragment(agendaItemType))
        }
    }
}