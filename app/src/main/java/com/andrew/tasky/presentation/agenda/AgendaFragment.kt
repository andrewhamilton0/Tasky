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
import com.andrew.tasky.domain.AgendaItem
import com.andrew.tasky.presentation.adapters.AgendaItemAdapter
import com.andrew.tasky.presentation.adapters.MiniCalendarAdapter
import com.andrew.tasky.presentation.dialogs.DatePickerDialog
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        fragmentAgendaBinding = FragmentAgendaBinding.bind(view)

        subscribeToObservables()
        setOnClickListeners()
        setupCurrentMonthTextView()
    }

    private fun subscribeToObservables() {
        fragmentAgendaBinding.miniCalendar.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.HORIZONTAL, false
        )
        collectLatestLifecycleFlow(viewModel.dateSelected) { dateSelected ->
            setupCurrentDateSelectedTextView(dateSelected)
            setupAgendaItemListRecyclerView(dateSelected)

            val adapter = MiniCalendarAdapter(
                startDate = currentDate,
                calendarSize = 6,
                dateSelected = dateSelected,
                onHolderClick = { dateClicked -> viewModel.setDateSelected(dateClicked) }
            )
            fragmentAgendaBinding.miniCalendar.adapter = adapter
        }
    }

    private fun setOnClickListeners() {

        fragmentAgendaBinding.apply {

            calendarMonth.setOnClickListener {
                showDatePickerFragment()
            }
            calendarDropDownArrow.setOnClickListener {
                showDatePickerFragment()
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
                            val agendaItemType = AgendaItemType.REMINDER
                            navController.navigate(
                                AgendaFragmentDirections
                                    .actionAgendaFragmentToAgendaItemDetailFragment(
                                        null,
                                        agendaItemType,
                                        true
                                    )
                            )
                            true
                        }
                        R.id.task -> {
                            val agendaItemType = AgendaItemType.TASK
                            navController.navigate(
                                AgendaFragmentDirections
                                    .actionAgendaFragmentToAgendaItemDetailFragment(
                                        null,
                                        agendaItemType,
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

    private fun setupAgendaItemListRecyclerView(dateSelected: LocalDate) {
        val sortedAgendaItems = viewModel.sortByDateSelected(dateSelected)

        val adapter = AgendaItemAdapter(
            agendaItems = sortedAgendaItems,
            onAgendaItemOptionClick = { agendaItem, agendaItemActionOptions ->
                agendaItemOptions(agendaItem, agendaItemActionOptions)
            }
        )
        fragmentAgendaBinding.agendaItemRecyclerView.adapter = adapter
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
            AgendaItemType.REMINDER -> TODO()
        }
    }

    private fun showDatePickerFragment() {
        val datePickerFragment = DatePickerDialog()
        val supportFragmentManager = requireActivity().supportFragmentManager

        supportFragmentManager.setFragmentResultListener(
            "REQUEST_KEY",
            viewLifecycleOwner
        ) {
            resultKey, bundle ->
            if (resultKey == "REQUEST_KEY") {
                val date = bundle.getString("SELECTED_DATE")
                val formatter = DateTimeFormatter.ofPattern("MMM dd yyyy")
                viewModel.setDateSelected(LocalDate.parse(date, formatter))
            }
        }
        datePickerFragment.show(supportFragmentManager, "DatePickerFragment")
    }
}
