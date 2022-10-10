package com.andrew.tasky.presentation.agenda_item_detail

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
import com.andrew.tasky.util.AgendaItemType
import com.andrew.tasky.util.EditType
import com.andrew.tasky.util.collectLatestLifecycleFlow
import java.time.LocalDateTime
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
        agendaItemType = AgendaItemType.valueOf(args.agendaItemType)

        subscribeToObservables()
        setupAgendaItemType()
        onClickListeners()
    }

    private fun subscribeToObservables() {
        collectLatestLifecycleFlow(viewModel.isInEditMode) { editMode ->
            setEditMode(editMode)
        }
        collectLatestLifecycleFlow(viewModel.title) { taskTitle ->
            binding.taskTitle.text = taskTitle
        }
        collectLatestLifecycleFlow(viewModel.description) { description ->
            binding.descriptionTextView.text = description
        }
        collectLatestLifecycleFlow(viewModel.selectedTime) { time ->
            binding.time.text = time
        }
        collectLatestLifecycleFlow(viewModel.selectedDate) { date ->
            binding.date.text = date
        }
        collectLatestLifecycleFlow(viewModel.selectedReminderTime) { reminderTime ->
            binding.reminderTextView.text = getString(reminderTime)
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
                viewModel.setSelectedTime(time)
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
                viewModel.setSelectedDate(date)
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
                        viewModel.setSelectedReminderTime(R.string.ten_minutes_before)
                        true
                    }
                    R.id.thirtyMinutes -> {
                        viewModel.setSelectedReminderTime(R.string.thirty_minutes_before)
                        true
                    }
                    R.id.oneHour -> {
                        viewModel.setSelectedReminderTime(R.string.one_hour_before)
                        true
                    }
                    R.id.sixHours -> {
                        viewModel.setSelectedReminderTime(R.string.six_hours_before)
                        true
                    }
                    R.id.oneDay -> {
                        viewModel.setSelectedReminderTime(R.string.one_day_before)
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
        val bundle1 = Bundle()
        bundle1.putString("AGENDA_ITEM_TYPE", agendaItemType.name)
        supportFragmentManager.setFragmentResult("DELETE_CONFIRMATION_AGENDA_TYPE_REQUEST_KEY", bundle1)

        //Shows deleteConfirmationDialogFragment
        deleteConfirmationDialogFragment.show(supportFragmentManager, "DeleteDialogFragment")
    }
}