package com.andrew.tasky.presentation.agenda

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrew.tasky.R
import com.andrew.tasky.databinding.FragmentAgendaBinding
import com.andrew.tasky.domain.models.AgendaItem
import com.andrew.tasky.presentation.adapters.AgendaItemAdapter
import com.andrew.tasky.presentation.adapters.MiniCalendarAdapter
import com.andrew.tasky.util.*
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class AgendaFragment : Fragment(R.layout.fragment_agenda) {

    private lateinit var navController: NavController
    private val viewModel: AgendaViewModel by viewModels()
    private lateinit var fragmentAgendaBinding: FragmentAgendaBinding
    private var currentDateAndTime: LocalDateTime = LocalDateTime.now()
    private var currentDate = currentDateAndTime.toLocalDate()

    private lateinit var agendaAdapter: AgendaItemAdapter
    private lateinit var miniCalendarAdapter: MiniCalendarAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        fragmentAgendaBinding = FragmentAgendaBinding.bind(view)

        subscribeToObservables()
        setOnClickListeners()
        setupCurrentMonthTextView()
        setupAgendaItemListRecyclerView()
        miniCalendarAdapter = MiniCalendarAdapter(
            onDateClick = { date -> viewModel.setDateSelected(date) }
        )
        fragmentAgendaBinding.miniCalendar.adapter = miniCalendarAdapter
        fragmentAgendaBinding.miniCalendar.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.HORIZONTAL, false
        )
    }

    private fun subscribeToObservables() {
        collectLatestLifecycleFlow(viewModel.calendarDateItemList) { dates ->
            miniCalendarAdapter.submitList(dates)
        }
        collectLatestLifecycleFlow(viewModel.dateSelected) { dateSelected ->
            setupCurrentDateSelectedTextView(dateSelected)
        }
        collectLatestLifecycleFlow(viewModel.agendaItems) { items ->
            agendaAdapter.submitList(items)
        }
    }

    private fun setOnClickListeners() {

        fragmentAgendaBinding.apply {

            calendarMonth.setOnClickListener {
                showDatePickerDialog(viewModel::setDateSelected)
            }
            calendarDropDownArrow.setOnClickListener {
                showDatePickerDialog(viewModel::setDateSelected)
            }

            logoutButton.setOnClickListener { view ->
                val popupMenu = PopupMenu(requireContext(), view)
                popupMenu.inflate(R.menu.menu_logout)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.logout -> {
                            navController.navigate(
                                AgendaFragmentDirections
                                    .actionAgendaFragmentToLoginFragment()
                            )
                            true
                        }
                        else -> true
                    }
                }
                popupMenu.show()
            }

            addAgendaItemFAB.setOnClickListener { view ->
                val popupMenu = PopupMenu(requireContext(), view)
                popupMenu.inflate(R.menu.menu_add_agenda_item)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.event -> {
                            navController.navigate(
                                AgendaFragmentDirections
                                    .actionAgendaFragmentToEventDetailFragment(
                                        null,
                                        true
                                    )
                            )
                            true
                        }
                        R.id.reminder -> {
                            navController.navigate(
                                AgendaFragmentDirections
                                    .actionAgendaFragmentToReminderDetailFragment(
                                        null,
                                        true
                                    )
                            )
                            true
                        }
                        R.id.task -> {
                            // Todo()
                            true
                        }
                        else -> true
                    }
                }
                popupMenu.show()
            }
        }
    }

    private fun setupCurrentMonthTextView() {
        fragmentAgendaBinding.calendarMonth.text = currentDate
            .format(DateTimeFormatter.ofPattern("MMMM")).uppercase()
    }

    private fun setupCurrentDateSelectedTextView(dateSelected: LocalDate) {

        when (dateSelected) {
            currentDate.minusDays(1) -> {
                fragmentAgendaBinding.currentDateSelectedTextView.text =
                    getString(R.string.yesterday)
            }
            currentDate -> {
                fragmentAgendaBinding.currentDateSelectedTextView.text = getString(R.string.today)
            }
            currentDate.plusDays(1) -> {
                fragmentAgendaBinding.currentDateSelectedTextView.text =
                    getString(R.string.tomorrow)
            }
            else -> {
                fragmentAgendaBinding.currentDateSelectedTextView.text = dateSelected
                    .format(DateTimeFormatter.ofPattern("MMM dd yyyy"))
            }
        }
    }

    private fun setupAgendaItemListRecyclerView() {
        agendaAdapter = AgendaItemAdapter(
            onAgendaItemOptionClick = { agendaItem, agendaItemActionOptions ->
                agendaItemOptions(agendaItem, agendaItemActionOptions)
            }
        )
        fragmentAgendaBinding.agendaItemRecyclerView.adapter = agendaAdapter
        fragmentAgendaBinding.agendaItemRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
    }

    private fun agendaItemOptions(
        agendaItem: AgendaItem,
        selectedAgendaItemMenuOption: AgendaItemMenuOption
    ) {
        when (selectedAgendaItemMenuOption) {
            AgendaItemMenuOption.OPEN -> {
                openAgendaItemDetail(agendaItem = agendaItem, isInEditMode = false)
            }
            AgendaItemMenuOption.EDIT -> {
                openAgendaItemDetail(agendaItem = agendaItem, isInEditMode = true)
            }
            AgendaItemMenuOption.DELETE -> {
                viewModel.deleteAgendaItem(agendaItem = agendaItem)
            }
        }
    }

    private fun openAgendaItemDetail(agendaItem: AgendaItem, isInEditMode: Boolean) {

        when (agendaItem.type) {
            AgendaItemType.TASK -> TODO()
            AgendaItemType.EVENT ->
                navController.navigate(
                    AgendaFragmentDirections
                        .actionAgendaFragmentToEventDetailFragment(
                            agendaItem,
                            isInEditMode
                        )
                )
            AgendaItemType.REMINDER -> navController.navigate(
                AgendaFragmentDirections
                    .actionAgendaFragmentToReminderDetailFragment(
                        agendaItem,
                        isInEditMode
                    )
            )
        }
    }
}
