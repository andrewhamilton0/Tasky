package com.andrew.tasky.agenda.presentation.screens.agenda

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrew.tasky.R
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.presentation.adapters.AgendaItemAdapter
import com.andrew.tasky.agenda.presentation.adapters.MiniCalendarAdapter
import com.andrew.tasky.agenda.util.*
import com.andrew.tasky.databinding.FragmentAgendaBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class AgendaFragment : Fragment(R.layout.fragment_agenda) {

    private lateinit var navController: NavController
    private val viewModel: AgendaViewModel by viewModels()
    private lateinit var fragmentAgendaBinding: FragmentAgendaBinding

    private lateinit var agendaAdapter: AgendaItemAdapter
    private lateinit var miniCalendarAdapter: MiniCalendarAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        fragmentAgendaBinding = FragmentAgendaBinding.bind(view)

        fragmentAgendaBinding.logoutButtonText.text = viewModel.nameInitials
        subscribeToObservables()
        setOnClickListeners()
        setupAgendaItemListRecyclerView()
        miniCalendarAdapter = MiniCalendarAdapter(
            onDateClick = viewModel::setDateSelected
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
        collectLatestLifecycleFlow(viewModel.uiAgendaItems) { items ->
            agendaAdapter.submitList(items)
        }
        collectLatestLifecycleFlow(viewModel.currentDateAndTimeFlow) { currentDateAndTime ->
            setupCurrentMonthTextView(currentDate = currentDateAndTime.toLocalDate())
        }
        collectLatestLifecycleFlow(viewModel.currentDateType) { dateType ->
            setupCurrentDateSelectedTextView(dateType)
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
                            viewModel.logout()
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
                            navController.navigate(
                                AgendaFragmentDirections
                                    .actionAgendaFragmentToTaskDetailFragment(
                                        null,
                                        true
                                    )
                            )
                            true
                        }
                        else -> true
                    }
                }
                popupMenu.show()
            }
        }
    }

    private fun setupCurrentMonthTextView(currentDate: LocalDate) {
        fragmentAgendaBinding.calendarMonth.text = currentDate
            .format(DateTimeFormatter.ofPattern("MMMM")).uppercase()
    }

    private fun setupCurrentDateSelectedTextView(dateType: DateType) {
        when (dateType) {
            is DateType.FullDate ->
                fragmentAgendaBinding.currentDateSelectedTextView.text =
                    dateType.date.format(DateTimeFormatter.ofPattern("MMM dd yyyy"))
            DateType.Today ->
                fragmentAgendaBinding.currentDateSelectedTextView.text =
                    getString(R.string.today)
            DateType.Tomorrow ->
                fragmentAgendaBinding.currentDateSelectedTextView.text =
                    getString(R.string.tomorrow)
            DateType.Yesterday ->
                fragmentAgendaBinding.currentDateSelectedTextView.text =
                    getString(R.string.yesterday)
        }
    }

    private fun setupAgendaItemListRecyclerView() {
        agendaAdapter = AgendaItemAdapter(
            onAgendaItemCardClick = { agendaItem ->
                openAgendaItemDetail(agendaItem = agendaItem, isInEditMode = false)
            },
            onAgendaItemOptionClick = { agendaItem, view ->
                agendaItemOptions(agendaItem = agendaItem, view = view)
            },
            onDoneButtonClick = (viewModel::switchIsDone)
        )
        fragmentAgendaBinding.agendaItemRecyclerView.adapter = agendaAdapter
        fragmentAgendaBinding.agendaItemRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
    }

    private fun agendaItemOptions(agendaItem: AgendaItem, view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.inflate(R.menu.menu_agenda_item_actions)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.open -> {
                    openAgendaItemDetail(agendaItem = agendaItem, isInEditMode = false)
                    true
                }
                R.id.edit -> {
                    openAgendaItemDetail(agendaItem = agendaItem, isInEditMode = true)
                    true
                }
                R.id.delete -> {
                    viewModel.deleteAgendaItem(agendaItem = agendaItem)
                    true
                }
                else -> true
            }
        }
        popupMenu.show()
    }

    private fun openAgendaItemDetail(agendaItem: AgendaItem, isInEditMode: Boolean) {
        when (agendaItem) {
            is AgendaItem.Task -> navController.navigate(
                AgendaFragmentDirections
                    .actionAgendaFragmentToTaskDetailFragment(
                        agendaItem.id,
                        isInEditMode
                    )
            )
            is AgendaItem.Event ->
                navController.navigate(
                    AgendaFragmentDirections
                        .actionAgendaFragmentToEventDetailFragment(
                            agendaItem.id,
                            isInEditMode
                        )
                )
            is AgendaItem.Reminder -> navController.navigate(
                AgendaFragmentDirections
                    .actionAgendaFragmentToReminderDetailFragment(
                        agendaItem.id,
                        isInEditMode
                    )
            )
        }
    }
}
