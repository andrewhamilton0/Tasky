package com.andrew.tasky.presentation.agenda

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.PopupMenu
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrew.tasky.R
import com.andrew.tasky.data.AgendaItem
import com.andrew.tasky.databinding.FragmentAgendaBinding
import com.andrew.tasky.databinding.ItemMiniCalendarDayBinding
import com.andrew.tasky.presentation.adapter.AgendaItemAdapter
import com.andrew.tasky.presentation.dialogs.DatePickerFragment
import com.andrew.tasky.util.AgendaItemType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AgendaFragment : Fragment(R.layout.fragment_agenda) {

    private lateinit var navController: NavController
    private lateinit var viewModel: AgendaViewModel
    private lateinit var fragmentAgendaBinding: FragmentAgendaBinding
    private var currentDateAndTime: LocalDateTime = LocalDateTime.now()
    private var currentDate = currentDateAndTime.toLocalDate()
    private val agendaItemList = mutableListOf(
        AgendaItem(
            AgendaItemType.EVENT, false, "Meeting",
            "Project meeting with Abby", LocalDateTime.of(2022, 10, 8, 8, 22),
            LocalDateTime.of(2022, 10, 8, 8, 32)
        ),
        AgendaItem(
            AgendaItemType.REMINDER, false, "Take out trash",
            "Take trash from inside to outside", LocalDateTime.of(2022, 11, 7, 8, 37)
        ),
        AgendaItem(
            AgendaItemType.TASK, true, "Code",
            "Finish coding lesson", LocalDateTime.of(2022, 10, 7, 9, 22)
        ),
        AgendaItem(
            AgendaItemType.EVENT, false, "Run",
            "Go for an evening run", LocalDateTime.of(2022, 10, 7, 12, 22),
            LocalDateTime.of(2022, 10, 7, 14, 22)
        ),
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        viewModel = ViewModelProvider(this)[AgendaViewModel::class.java]
        fragmentAgendaBinding = FragmentAgendaBinding.bind(view)

        subscribeToObservables()
        setOnClickListeners()
        setupCurrentMonthTextView()
    }

    private fun <T> Fragment.collectLatestLifecycleFlow(
        flow: Flow<T>,
        onCollect: suspend (T) -> Unit
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collectLatest {
                    onCollect(it)
                }
            }
        }
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
            logoutButton.setOnClickListener {
                val popupMenu = PopupMenu(requireContext(), it)
                popupMenu.inflate(R.menu.menu_logout)
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
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
            addAgendaItemFAB.setOnClickListener {
                val popupMenu = PopupMenu(requireContext(), it)
                popupMenu.inflate(R.menu.menu_add_agenda_item)
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.event -> {
                            val agendaItemType = AgendaItemType.EVENT.name
                            navController.navigate(
                                AgendaFragmentDirections
                                    .actionAgendaFragmentToAgendaItemDetailFragment(agendaItemType)
                            )
                            true
                        }
                        R.id.reminder -> {
                            val agendaItemType = AgendaItemType.REMINDER.name
                            navController.navigate(
                                AgendaFragmentDirections
                                    .actionAgendaFragmentToAgendaItemDetailFragment(agendaItemType)
                            )
                            true
                        }
                        R.id.task -> {
                            val agendaItemType = AgendaItemType.TASK.name
                            navController.navigate(
                                AgendaFragmentDirections
                                    .actionAgendaFragmentToAgendaItemDetailFragment(agendaItemType)
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
        ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.today).dateNumber.text =
            todayDate.format(DateTimeFormatter.ofPattern("d"))
        ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.tomorrow).dateNumber.text =
            tomorrowDate.format(DateTimeFormatter.ofPattern("d"))
        ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.thirdDay).dateNumber.text =
            thirdDayDate.format(DateTimeFormatter.ofPattern("d"))
        ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.fourthDay).dateNumber.text =
            fourthDayDate.format(DateTimeFormatter.ofPattern("d"))
        ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.fifthDay).dateNumber.text =
            fifthDayDate.format(DateTimeFormatter.ofPattern("d"))
        ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.sixthDay).dateNumber.text =
            sixthDayDate.format(DateTimeFormatter.ofPattern("d"))

        //sets up dayOfWeek text view in mini calendar
        ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.today).dayOfWeek.text =
            todayDate.format(DateTimeFormatter.ofPattern("eeeee"))
        ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.tomorrow).dayOfWeek.text =
            tomorrowDate.format(DateTimeFormatter.ofPattern("eeeee"))
        ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.thirdDay).dayOfWeek.text =
            thirdDayDate.format(DateTimeFormatter.ofPattern("eeeee"))
        ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.fourthDay).dayOfWeek.text =
            fourthDayDate.format(DateTimeFormatter.ofPattern("eeeee"))
        ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.fifthDay).dayOfWeek.text =
            fifthDayDate.format(DateTimeFormatter.ofPattern("eeeee"))
        ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.sixthDay).dayOfWeek.text =
            sixthDayDate.format(DateTimeFormatter.ofPattern("eeeee"))

        //If dateSelected is on miniCalendar, it sets the background color to yellow
        if (dateSelected == todayDate) {
            ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.today).miniCalendarCard
                .setCardBackgroundColor(Color.parseColor("#fdeda7"))
        } else {
            ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.today).miniCalendarCard
                .setCardBackgroundColor(Color.parseColor("#FFFFFFFF"))
        }
        if (dateSelected == tomorrowDate) {
            ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.tomorrow).miniCalendarCard
                .setCardBackgroundColor(Color.parseColor("#fdeda7"))
        } else {
            ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.tomorrow).miniCalendarCard
                .setCardBackgroundColor(Color.parseColor("#FFFFFFFF"))
        }
        if (dateSelected == thirdDayDate) {
            ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.thirdDay).miniCalendarCard
                .setCardBackgroundColor(Color.parseColor("#fdeda7"))
        } else {
            ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.thirdDay).miniCalendarCard
                .setCardBackgroundColor(Color.parseColor("#FFFFFFFF"))
        }
        if (dateSelected == fourthDayDate) {
            ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.fourthDay).miniCalendarCard
                .setCardBackgroundColor(Color.parseColor("#fdeda7"))
        } else {
            ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.fourthDay).miniCalendarCard
                .setCardBackgroundColor(Color.parseColor("#FFFFFFFF"))
        }
        if (dateSelected == fifthDayDate) {
            ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.fifthDay).miniCalendarCard
                .setCardBackgroundColor(Color.parseColor("#fdeda7"))
        } else {
            ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.fifthDay).miniCalendarCard
                .setCardBackgroundColor(Color.parseColor("#FFFFFFFF"))
        }
        if (dateSelected == sixthDayDate) {
            ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.sixthDay).miniCalendarCard
                .setCardBackgroundColor(Color.parseColor("#fdeda7"))
        } else {
            ItemMiniCalendarDayBinding.bind(fragmentAgendaBinding.sixthDay).miniCalendarCard
                .setCardBackgroundColor(Color.parseColor("#FFFFFFFF"))
        }
    }

    //Changes currentDateSelectedTextView to yesterday, today, tomorrow, or formatted date
    private fun setupCurrentDateSelectedTextView(dateSelected: LocalDate){
        val yesterdayDate = currentDate.minusDays(1)
        val tomorrowDate = currentDate.plusDays(1)

        if (dateSelected == yesterdayDate){
            fragmentAgendaBinding.currentDateSelectedTextView.text = getString(R.string.yesterday)
        }
        else if (dateSelected == currentDate){
            fragmentAgendaBinding.currentDateSelectedTextView.text = getString(R.string.today)
        }
        else if (dateSelected == tomorrowDate){
            fragmentAgendaBinding.currentDateSelectedTextView.text = getString(R.string.tomorrow)
        }
        else {
            fragmentAgendaBinding.currentDateSelectedTextView.text = dateSelected
                .format(DateTimeFormatter.ofPattern("MMM dd yyyy"))
        }
    }

    private fun setupAgendaItemListRecyclerView(dateSelected: LocalDate){
        val agendaItemListSorted = mutableListOf<AgendaItem>()
        agendaItemList.sortedBy{ agendaItem -> agendaItem.startDateAndTime }
            .forEach{if (it.startDateAndTime.toLocalDate() == dateSelected){
                agendaItemListSorted.add(it)}}
        val adapter = AgendaItemAdapter(agendaItemListSorted)

        fragmentAgendaBinding.agendaItemRecyclerView.adapter = adapter
        fragmentAgendaBinding.agendaItemRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
    }

    private fun showDatePickerFragment(){
        val datePickerFragment = DatePickerFragment()
        val supportFragmentManager = requireActivity().supportFragmentManager

        supportFragmentManager.setFragmentResultListener(
            "REQUEST_KEY",
            viewLifecycleOwner
        ){
                resultKey, bundle -> if(resultKey == "REQUEST_KEY"){
            val date = bundle.getString("SELECTED_DATE")
            val formatter = DateTimeFormatter.ofPattern("MMM dd yyyy")
            viewModel.setDateSelected(LocalDate.parse(date, formatter))
        }
        }
        datePickerFragment.show(supportFragmentManager, "DatePickerFragment")
    }
}