package com.andrew.tasky.presentation.agenda_item_detail

import android.graphics.Paint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.andrew.tasky.R
import com.andrew.tasky.databinding.FragmentAgendaItemDetailBinding
import com.andrew.tasky.presentation.dialogs.DatePickerFragment
import com.andrew.tasky.presentation.dialogs.DeleteConfirmationDialogFragment
import com.andrew.tasky.presentation.dialogs.TimePickerFragment
import com.andrew.tasky.util.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AgendaItemDetailFragment: Fragment(R.layout.fragment_agenda_item_detail) {

    private val currentDate = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("dd MMMM yyyy")).uppercase()

    private lateinit var viewModel: AgendaItemDetailViewModel
    private lateinit var navController: NavController
    private lateinit var binding: FragmentAgendaItemDetailBinding
    private lateinit var agendaItemType: AgendaItemType
    private val args: AgendaItemDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAgendaItemDetailBinding.bind(view)
        navController = Navigation.findNavController(view)
        viewModel = ViewModelProvider(this)[AgendaItemDetailViewModel::class.java]
        agendaItemType = args.agendaItemType

        subscribeToObservables()
        setupAgendaItemType()
        onClickListeners()
    }

    private fun subscribeToObservables() {
        binding.apply {
            collectLatestLifecycleFlow(viewModel.isInEditMode) { editMode ->
                setEditMode(editMode)
            }
            collectLatestLifecycleFlow(viewModel.isDone) { isDone ->
                if (isDone) {
                    taskDoneCircle.setBackgroundResource(R.drawable.task_done_circle)
                    taskTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    taskDoneCircle.setBackgroundResource(R.drawable.task_undone_circle)
                    binding.taskTitle.paintFlags = Paint.ANTI_ALIAS_FLAG
                }
            }
            collectLatestLifecycleFlow(viewModel.title) { title ->
                taskTitle.text = title
            }
            collectLatestLifecycleFlow(viewModel.description) { description ->
                descriptionTextView.text = description
            }
            collectLatestLifecycleFlow(viewModel.selectedTime) { selectedTime ->
                time.text = selectedTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
            }
            collectLatestLifecycleFlow(viewModel.selectedDate) { selectedDate ->
                date.text = selectedDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy"))
            }
            collectLatestLifecycleFlow(viewModel.selectedReminderTime) { reminderTime ->
                when (reminderTime) {
                    ReminderTimes.TEN_MINUTES_BEFORE ->
                        reminderTextView.text = getString(R.string.ten_minutes_before)
                    ReminderTimes.THIRTY_MINUTES_BEFORE ->
                        reminderTextView.text = getString(R.string.thirty_minutes_before)
                    ReminderTimes.ONE_HOUR_BEFORE ->
                        reminderTextView.text = getString(R.string.one_hour_before)
                    ReminderTimes.SIX_HOURS_BEFORE ->
                        reminderTextView.text = getString(R.string.six_hours_before)
                    ReminderTimes.ONE_DAY_BEFORE ->
                        reminderTextView.text = getString(R.string.one_day_before)
                }
            }
        }
    }

    private fun setupAgendaItemType(){
        binding.apply {
            when (agendaItemType) {
                AgendaItemType.TASK -> {
                    taskColorBox.setImageResource(R.drawable.task_icon_box)
                    taskType.text = getString(R.string.task)
                }
                AgendaItemType.EVENT -> {
                    taskColorBox.setImageResource(R.drawable.event_icon_box)
                    taskType.text = getString(R.string.event)
                }
                AgendaItemType.REMINDER -> {
                    taskColorBox.setImageResource(R.drawable.reminder_icon_box)
                    taskType.text = getString(R.string.reminder)
                }
            }
        }
        args.agendaItem?.let {
            viewModel.setTitle(it.title)
            viewModel.setDescription(it.description)
            viewModel.setSelectedDate(it.startDateAndTime.toLocalDate())
            viewModel.setSelectedTime(it.startDateAndTime.toLocalTime())
            viewModel.setIsDone(it.isDone)
            viewModel.setSelectedReminderTime(it.reminderTime)
        }
        viewModel.setEditMode(args.isInEditMode)
    }

    private fun onClickListeners(){
        binding.apply {
            closeButton.setOnClickListener {
                navController.popBackStack()
            }

            editButton.setOnClickListener {
                viewModel.setEditMode(true)
            }

            saveButton.setOnClickListener {
                viewModel.setEditMode(false)
            }

            taskDoneCircle.setOnClickListener{
                viewModel.setIsDone(!viewModel.isDone.value)
            }

            taskTitle.setOnClickListener {
                navigateToEditFragment(EditType.TITLE)
            }

            editTitleButton.setOnClickListener {
                navigateToEditFragment(EditType.TITLE)
            }

            editDescriptionButton.setOnClickListener {
                navigateToEditFragment(EditType.DESCRIPTION)
            }

            descriptionTextView.setOnClickListener {
                navigateToEditFragment(EditType.DESCRIPTION)
            }

            time.setOnClickListener {
                showTimePickerFragment()
            }

            timeButton.setOnClickListener{
                showTimePickerFragment()
            }

            date.setOnClickListener {
                showDatePickerFragment()
            }

            dateButton.setOnClickListener {
                showDatePickerFragment()
            }

            reminderTextView.setOnClickListener {
                showReminderOptionsPopupMenu(it)
            }

            reminderButton.setOnClickListener {
                showReminderOptionsPopupMenu(it)
            }

            deleteTaskButton.setOnClickListener{
                showDeleteConfirmationDialogFragment()
            }
        }
    }

    private fun navigateToEditFragment(editType: EditType){
        setFragmentResultListener("INPUT_REQUEST_KEY"){
            resultKey, bundle -> if(resultKey == "INPUT_REQUEST_KEY"){
                val input = bundle.getString("INPUT")
                when(editType){
                    EditType.TITLE -> {
                        if (input != null) {
                            viewModel.setTitle(input)
                        }
                    }
                    EditType.DESCRIPTION -> {
                        if (input != null) {
                            viewModel.setDescription(input)
                        }
                    }
                }
            }
        }
        when(editType){
            EditType.TITLE -> {
                val text = binding.taskTitle.text.toString()
                val bundle = Bundle()
                bundle.putString("TEXT", text)
                bundle.putString("EDIT_TYPE", editType.name)

                setFragmentResult("EDIT_TYPE_AND_TEXT_REQUEST_KEY", bundle)

                navController.navigate(AgendaItemDetailFragmentDirections.actionAgendaItemDetailFragmentToEditFragment())
            }
            EditType.DESCRIPTION -> {
                val text = binding.descriptionTextView.text.toString()
                val bundle = Bundle()
                bundle.putString("TEXT", text)
                bundle.putString("EDIT_TYPE", editType.name)

                setFragmentResult("EDIT_TYPE_AND_TEXT_REQUEST_KEY", bundle)

                navController.navigate(AgendaItemDetailFragmentDirections.actionAgendaItemDetailFragmentToEditFragment())
            }
        }
    }

    private fun showTimePickerFragment(){
        val timePickerFragment = TimePickerFragment()
        val supportFragmentManager = requireActivity().supportFragmentManager

        supportFragmentManager.setFragmentResultListener(
            "REQUEST_KEY",
            viewLifecycleOwner
        ){
            resultKey, bundle -> if(resultKey == "REQUEST_KEY"){
                val time = bundle.getString("SELECTED_TIME")
                val formatter = DateTimeFormatter.ofPattern("hh:mm a")
                viewModel.setSelectedTime(LocalTime.parse(time, formatter))
            }
        }
        timePickerFragment.show(supportFragmentManager, "TimePickerFragment")
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
                viewModel.setSelectedDate(LocalDate.parse(date, formatter))
                }
        }
        datePickerFragment.show(supportFragmentManager, "DatePickerFragment")
    }

    private fun setEditMode(isEditing: Boolean){
        binding.apply {
            if (isEditing){
                when(agendaItemType){
                    AgendaItemType.TASK ->
                        currentDateTextView.text = getString(R.string.edit_task_text)
                    AgendaItemType.EVENT ->
                        currentDateTextView.text = getString(R.string.edit_event_text)
                    AgendaItemType.REMINDER ->
                        currentDateTextView.text = getString(R.string.edit_reminder_text)
                }
            }
            else{
                currentDateTextView.text = currentDate
            }

            saveButton.isVisible = isEditing
            saveButton.isEnabled = isEditing

            editButton.isVisible = !isEditing
            editButton.isEnabled = !isEditing

            editTitleButton.isVisible = isEditing
            editTitleButton.isEnabled = isEditing
            taskTitle.isEnabled = isEditing

            editDescriptionButton.isVisible = isEditing
            editDescriptionButton.isEnabled = isEditing
            descriptionTextView.isEnabled = isEditing

            time.isEnabled = isEditing
            timeButton.isVisible = isEditing
            timeButton.isEnabled=isEditing
            date.isEnabled = isEditing
            dateButton.isVisible = isEditing
            dateButton.isEnabled = isEditing

            reminderTextView.isEnabled = isEditing
            reminderButton.isEnabled = isEditing
            reminderButton.isVisible = isEditing
        }
    }

    //View parameter determines which view popup appears under
    private fun showReminderOptionsPopupMenu(view: View){
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.inflate(R.menu.menu_reminder_time_options)
        binding.reminderTextView.apply {
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.tenMinutes -> {
                        viewModel.setSelectedReminderTime(ReminderTimes.TEN_MINUTES_BEFORE)
                        true
                    }
                    R.id.thirtyMinutes -> {
                        viewModel.setSelectedReminderTime(ReminderTimes.THIRTY_MINUTES_BEFORE)
                        true
                    }
                    R.id.oneHour -> {
                        viewModel.setSelectedReminderTime(ReminderTimes.ONE_HOUR_BEFORE)
                        true
                    }
                    R.id.sixHours -> {
                        viewModel.setSelectedReminderTime(ReminderTimes.SIX_HOURS_BEFORE)
                        true
                    }
                    R.id.oneDay -> {
                        viewModel.setSelectedReminderTime(ReminderTimes.ONE_DAY_BEFORE)
                        true
                    }
                    else -> true
                }
            }
        }
        popupMenu.show()
    }

    private fun showDeleteConfirmationDialogFragment(){
        val deleteConfirmationDialogFragment = DeleteConfirmationDialogFragment()
        val supportFragmentManager = requireActivity().supportFragmentManager

        //If deleteButton is pressed, deleteAgenda returns true and popBackStack() is ran
        supportFragmentManager.setFragmentResultListener(
            "REQUEST_KEY",
            viewLifecycleOwner
        ){
            resultKey, bundle -> if(resultKey == "REQUEST_KEY"){
                val deleteAgenda = bundle.getBoolean("DELETE_AGENDA_ITEM")
                if (deleteAgenda){
                    navController.popBackStack()
                }
            }
        }

        //Sends agendaItemType to deleteConfirmationDialogFragment
        val bundle = Bundle()
        bundle.putString("AGENDA_ITEM_TYPE", agendaItemType.name)
        supportFragmentManager.setFragmentResult("DELETE_CONFIRMATION_AGENDA_TYPE_REQUEST_KEY", bundle)

        //Shows deleteConfirmationDialogFragment
        deleteConfirmationDialogFragment.show(supportFragmentManager, "DeleteDialogFragment")
    }
}