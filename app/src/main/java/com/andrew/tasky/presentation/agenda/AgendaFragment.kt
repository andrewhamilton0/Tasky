package com.andrew.tasky.presentation.agenda

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.PopupMenu
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrew.tasky.R
import com.andrew.tasky.databinding.FragmentAgendaBinding
import com.andrew.tasky.databinding.ItemMiniCalendarDayBinding
import com.andrew.tasky.domain.AgendaItem
import com.andrew.tasky.domain.AgendaItems
import com.andrew.tasky.presentation.adapter.AgendaItemAdapter
import com.andrew.tasky.presentation.dialogs.DatePickerFragment
import com.andrew.tasky.util.AgendaItemActionOptions
import com.andrew.tasky.util.AgendaItemType
import com.andrew.tasky.util.FragmentCommunication
import com.andrew.tasky.util.collectLatestLifecycleFlow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AgendaFragment : Fragment(R.layout.fragment_agenda), FragmentCommunication {

    private lateinit var navController: NavController
    private lateinit var viewModel: AgendaViewModel
    private lateinit var fragmentAgendaBinding: FragmentAgendaBinding
    private var currentDateAndTime: LocalDateTime = LocalDateTime.now()
    private var currentDate = currentDateAndTime.toLocalDate()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        viewModel = ViewModelProvider(this)[AgendaViewModel::class.java]
        fragmentAgendaBinding = FragmentAgendaBinding.bind(view)

        subscribeToObservables()
        setOnClickListeners()
        setupCurrentMonthTextView()
    }

    private fun subscribeToObservables() {
        //miniCalendarSetup function call, passing the currently selected date
        collectLatestLifecycleFlow(viewModel.dateSelected) { dateSelected ->
            setupMiniCalendar(dateSelected)
            setupCurrentDateSelectedTextView(dateSelected)
            setupAgendaItemListRecyclerView(dateSelected)
        }
    }

    private fun setOnClickListeners() {

        //On click listeners for miniCalendar allowing user to quickly change to near future dates
        fragmentAgendaBinding.apply {
            today.setOnClickListener {
                viewModel.setDateSelected(
                    currentDate
                )
            }
            tomorrow.setOnClickListener {
                viewModel.setDateSelected(
                    currentDate.plusDays(1)
                )
            }
            thirdDay.setOnClickListener {
                viewModel.setDateSelected(
                    currentDate.plusDays(2)
                )
            }
            fourthDay.setOnClickListener {
                viewModel.setDateSelected(
                    currentDate.plusDays(3)
                )
            }
            fifthDay.setOnClickListener {
                viewModel.setDateSelected(
                    currentDate.plusDays(4)
                )
            }
            sixthDay.setOnClickListener {
                viewModel.setDateSelected(
                    currentDate.plusDays(5)
                )
            }

            //Will pop up dialog
            calendarMonth.setOnClickListener {
                showDatePickerFragment()
            }
            calendarDropDownArrow.setOnClickListener {
                showDatePickerFragment()
            }

            //shows popup menu allowing user to logout and return to login page
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

            //shows popup menu allowing user to add different agenda types
            addAgendaItemFAB.setOnClickListener { view ->
                val popupMenu = PopupMenu(requireContext(), view)
                popupMenu.inflate(R.menu.menu_add_agenda_item)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.event -> {
                            val agendaItemType = AgendaItemType.EVENT
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

    private fun setupCurrentMonthTextView(){
        fragmentAgendaBinding.calendarMonth.text = currentDate
            .format(DateTimeFormatter.ofPattern("MMMM")).uppercase()
    }

    //miniCalendar is declared in subscribeToObservables function
    //miniCalendar onClickListeners can be found in onClickListeners function
    private fun setupMiniCalendar(dateSelected: LocalDate) {

        // sets up dates of miniCalendar
        val todayDate = currentDate
        val tomorrowDate = currentDate.plusDays(1)
        val thirdDayDate = currentDate.plusDays(2)
        val fourthDayDate = currentDate.plusDays(3)
        val fifthDayDate = currentDate.plusDays(4)
        val sixthDayDate = currentDate.plusDays(5)

        //sets up dateNumber text view in the mini calendar
        val dateNumberFormatter = DateTimeFormatter.ofPattern("d")
        ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.today).dateNumber.text =
            todayDate.format(dateNumberFormatter)
        ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.tomorrow).dateNumber.text =
            tomorrowDate.format(dateNumberFormatter)
        ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.thirdDay).dateNumber.text =
            thirdDayDate.format(dateNumberFormatter)
        ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.fourthDay).dateNumber.text =
            fourthDayDate.format(dateNumberFormatter)
        ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.fifthDay).dateNumber.text =
            fifthDayDate.format(dateNumberFormatter)
        ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.sixthDay).dateNumber.text =
            sixthDayDate.format(dateNumberFormatter)

        //sets up dayOfWeek text view in mini calendar
        val dayOfWeekFormatter = DateTimeFormatter.ofPattern("eeeee")
        ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.today).dayOfWeek.text =
            todayDate.format(dayOfWeekFormatter)
        ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.tomorrow).dayOfWeek.text =
            tomorrowDate.format(dayOfWeekFormatter)
        ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.thirdDay).dayOfWeek.text =
            thirdDayDate.format(dayOfWeekFormatter)
        ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.fourthDay).dayOfWeek.text =
            fourthDayDate.format(dayOfWeekFormatter)
        ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.fifthDay).dayOfWeek.text =
            fifthDayDate.format(dayOfWeekFormatter)
        ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.sixthDay).dayOfWeek.text =
            sixthDayDate.format(dayOfWeekFormatter)

        //If dateSelected is on miniCalendar, it sets the background color to yellow
        val highlightedColor = ResourcesCompat.getColor(
            resources,
            R.color.highlighted_mini_calendar,
            null
        )
        val unselectedColor = ResourcesCompat.getColor(
            resources,
            R.color.white,
            null
        )
        if (dateSelected == todayDate) {
            ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.today).miniCalendarCard
                .setCardBackgroundColor(highlightedColor)
        } else {
            ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.today).miniCalendarCard
                .setCardBackgroundColor(unselectedColor)
        }
        if (dateSelected == tomorrowDate) {
            ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.tomorrow).miniCalendarCard
                .setCardBackgroundColor(highlightedColor)
        } else {
            ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.tomorrow).miniCalendarCard
                .setCardBackgroundColor(unselectedColor)
        }
        if (dateSelected == thirdDayDate) {
            ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.thirdDay).miniCalendarCard
                .setCardBackgroundColor(highlightedColor)
        } else {
            ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.thirdDay).miniCalendarCard
                .setCardBackgroundColor(unselectedColor)
        }
        if (dateSelected == fourthDayDate) {
            ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.fourthDay).miniCalendarCard
                .setCardBackgroundColor(highlightedColor)
        } else {
            ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.fourthDay).miniCalendarCard
                .setCardBackgroundColor(unselectedColor)
        }
        if (dateSelected == fifthDayDate) {
            ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.fifthDay).miniCalendarCard
                .setCardBackgroundColor(highlightedColor)
        } else {
            ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.fifthDay).miniCalendarCard
                .setCardBackgroundColor(unselectedColor)
        }
        if (dateSelected == sixthDayDate) {
            ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.sixthDay).miniCalendarCard
                .setCardBackgroundColor(highlightedColor)
        } else {
            ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.sixthDay).miniCalendarCard
                .setCardBackgroundColor(unselectedColor)
        }
    }

    //Changes currentDateSelectedTextView to yesterday, today, tomorrow, or formatted date
    private fun setupCurrentDateSelectedTextView(dateSelected: LocalDate){
        val yesterdayDate = currentDate.minusDays(1)
        val tomorrowDate = currentDate.plusDays(1)

        when (dateSelected) {
            yesterdayDate -> {
                fragmentAgendaBinding.currentDateSelectedTextView.text = getString(R.string.yesterday)
            }
            currentDate -> {
                fragmentAgendaBinding.currentDateSelectedTextView.text = getString(R.string.today)
            }
            tomorrowDate -> {
                fragmentAgendaBinding.currentDateSelectedTextView.text = getString(R.string.tomorrow)
            }
            else -> {
                fragmentAgendaBinding.currentDateSelectedTextView.text = dateSelected
                    .format(DateTimeFormatter.ofPattern("MMM dd yyyy"))
            }
        }
    }

    private fun setupAgendaItemListRecyclerView(dateSelected: LocalDate){
        //Retrieves agenda items for selected date
        val sortedAgendaItems = AgendaItems.sortByDateSelected(dateSelected)

        //Binds retrieved agenda items and FragmentCommunication listener to agendaItemRecyclerView
        val adapter = AgendaItemAdapter(sortedAgendaItems, this)
        fragmentAgendaBinding.agendaItemRecyclerView.adapter = adapter
        fragmentAgendaBinding.agendaItemRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
    }

    //Retrieves data from AgendaItemListRecyclerView and uses it appropriately
    override fun respond(agendaItem: AgendaItem, actionOption: AgendaItemActionOptions) {
        val agendaItemType = agendaItem.type
        when (actionOption){
            AgendaItemActionOptions.OPEN ->{
                navController.navigate(
                    AgendaFragmentDirections
                        .actionAgendaFragmentToAgendaItemDetailFragment(
                            agendaItem,
                            agendaItemType,
                            false))
            }
            AgendaItemActionOptions.EDIT -> {
                navController.navigate(
                    AgendaFragmentDirections
                        .actionAgendaFragmentToAgendaItemDetailFragment(
                            agendaItem,
                            agendaItemType,
                            true
                        ))
            }
            AgendaItemActionOptions.DELETE -> {

            }
        }
    }

    private fun showDatePickerFragment(){
        val datePickerFragment = DatePickerFragment()
        val supportFragmentManager = requireActivity().supportFragmentManager

        supportFragmentManager.setFragmentResultListener(
            "REQUEST_KEY",
            viewLifecycleOwner
        ) {
            resultKey, bundle -> if(resultKey == "REQUEST_KEY"){
                val date = bundle.getString("SELECTED_DATE")
                val formatter = DateTimeFormatter.ofPattern("MMM dd yyyy")
                viewModel.setDateSelected(LocalDate.parse(date, formatter))
            }
        }
        datePickerFragment.show(supportFragmentManager, "DatePickerFragment")
    }
}