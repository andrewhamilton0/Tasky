package com.andrew.tasky.presentation.agenda

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrew.tasky.R
import com.andrew.tasky.data.AgendaItem
import com.andrew.tasky.databinding.FragmentAgendaBinding
import com.andrew.tasky.presentation.adapter.AgendaItemAdapter
import com.andrew.tasky.util.AgendaItemType
import java.time.LocalDateTime

class AgendaFragment : Fragment(R.layout.fragment_agenda) {

    private lateinit var navController: NavController
    private lateinit var viewModel: AgendaViewModel
    private lateinit var binding: FragmentAgendaBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        viewModel = ViewModelProvider(this).get(AgendaViewModel::class.java)
        binding = FragmentAgendaBinding.bind(view)

        var agendaItemList = mutableListOf(
            AgendaItem(AgendaItemType.EVENT, false, "Meeting",
                "Project meeting with Abby", LocalDateTime.of(2022, 10, 3, 8,22),
                LocalDateTime.of(2022, 10, 3, 8,32)),
            AgendaItem(AgendaItemType.REMINDER, false, "Take out trash",
                "Take trash from inside to outside", LocalDateTime.of(2022, 10, 3, 8,37)),
            AgendaItem(AgendaItemType.TASK, true, "Code",
                "Finish coding lesson", LocalDateTime.of(2022, 10, 3, 9,22)),
            AgendaItem(AgendaItemType.EVENT, false, "Run",
                "Go for an evening run", LocalDateTime.of(2022, 10, 3, 12,22),
                LocalDateTime.of(2022, 10, 15, 14,22)),
        )

        val adapter = AgendaItemAdapter(agendaItemList)

        binding.agendaItemRecyclerView.adapter = adapter
        binding.agendaItemRecyclerView.layoutManager = LinearLayoutManager(view.context)

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