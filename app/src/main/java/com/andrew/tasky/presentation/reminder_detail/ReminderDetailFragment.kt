package com.andrew.tasky.presentation.reminder_detail

import android.graphics.Paint
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.andrew.tasky.R
import com.andrew.tasky.databinding.FragmentReminderDetailBinding
import com.andrew.tasky.presentation.dialogs.DatePickerDialog
import com.andrew.tasky.presentation.dialogs.DeleteConfirmationDialog
import com.andrew.tasky.presentation.dialogs.TimePickerDialog
import com.andrew.tasky.util.*
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class ReminderDetailFragment : Fragment(R.layout.fragment_reminder_detail) {

    private val currentDate = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("dd MMMM yyyy")).uppercase()

    private val viewModel: ReminderDetailViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var binding: FragmentReminderDetailBinding
    private val args: ReminderDetailFragmentArgs by navArgs()
    private val agendaItemType = AgendaItemType.REMINDER

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentReminderDetailBinding.bind(view)
        navController = Navigation.findNavController(view)

        setupViewsAndOnClickListeners()
        subscribeToObservables()
    }

    private fun setupViewsAndOnClickListeners() {
        binding.apply {

            header.currentDateTextView.text = currentDate
            header.closeButton.setOnClickListener {
                navController.popBackStack()
            }
            header.editButton.setOnClickListener {
                viewModel.setEditMode(true)
            }
            header.saveButton.setOnClickListener {
                viewModel.saveAgendaItem()
                navController.popBackStack()
            }

            agendaItemTypeTVAndIconLayout.agendaItemIcon
                .setImageResource(R.drawable.ic_reminder_box)
            agendaItemTypeTVAndIconLayout.agendaItemTypeTextView.text = getString(R.string.reminder)

            addTitleAndDoneButtonLayout.taskDoneCircle.setOnClickListener {
                viewModel.setIsDone(!viewModel.isDone.value)
            }
            addTitleAndDoneButtonLayout.titleTextView.setOnClickListener {
                navigateToEditFragment(EditType.TITLE)
            }
            addTitleAndDoneButtonLayout.editTitleButton.setOnClickListener {
                navigateToEditFragment(EditType.TITLE)
            }

            addDescriptionLayout.editDescriptionButton.setOnClickListener {
                navigateToEditFragment(EditType.DESCRIPTION)
            }
            addDescriptionLayout.descriptionTextView.setOnClickListener {
                navigateToEditFragment(EditType.DESCRIPTION)
            }

            startTimeAndDateLayout.timeAndDateBeginningText.text = getString(R.string.from)
            startTimeAndDateLayout.timeTextView.setOnClickListener {
                showTimePickerDialog(it)
            }
            startTimeAndDateLayout.timeButton.setOnClickListener {
                showTimePickerDialog(it)
            }
            startTimeAndDateLayout.dateTextView.setOnClickListener {
                showDatePickerDialog(it)
            }
            startTimeAndDateLayout.dateButton.setOnClickListener {
                showDatePickerDialog(it)
            }

            reminderLayout.reminderTextView.setOnClickListener {
                showReminderOptionsPopupMenu(it)
            }
            reminderLayout.reminderButton.setOnClickListener {
                showReminderOptionsPopupMenu(it)
            }
            btmActionTvBtn.deleteAgendaItemButton.setOnClickListener {
                showDeleteConfirmationDialog()
            }
        }
    }

    private fun subscribeToObservables() {
        binding.apply {

            collectLatestLifecycleFlow(viewModel.isInEditMode) { isEditing ->
                header.saveButton.isVisible = isEditing
                header.saveButton.isEnabled = isEditing
                header.editButton.isVisible = !isEditing
                header.editButton.isEnabled = !isEditing

                addTitleAndDoneButtonLayout.editTitleButton.isVisible = isEditing
                addTitleAndDoneButtonLayout.editTitleButton.isEnabled = isEditing
                addTitleAndDoneButtonLayout.titleTextView.isEnabled = isEditing

                addDescriptionLayout.editDescriptionButton.isVisible = isEditing
                addDescriptionLayout.editDescriptionButton.isEnabled = isEditing
                addDescriptionLayout.descriptionTextView.isEnabled = isEditing

                startTimeAndDateLayout.timeTextView.isEnabled = isEditing
                startTimeAndDateLayout.timeButton.isEnabled = isEditing
                startTimeAndDateLayout.timeButton.isVisible = isEditing
                startTimeAndDateLayout.dateTextView.isEnabled = isEditing
                startTimeAndDateLayout.dateButton.isEnabled = isEditing
                startTimeAndDateLayout.dateButton.isVisible = isEditing

                reminderLayout.reminderTextView.isEnabled = isEditing
                reminderLayout.reminderButton.isEnabled = isEditing
                reminderLayout.reminderButton.isVisible = isEditing
            }

            collectLatestLifecycleFlow(viewModel.isDone) { isDone ->
                if (isDone) {
                    addTitleAndDoneButtonLayout.taskDoneCircle
                        .setBackgroundResource(R.drawable.task_done_circle)
                    addTitleAndDoneButtonLayout.titleTextView.paintFlags =
                        Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    addTitleAndDoneButtonLayout.taskDoneCircle
                        .setBackgroundResource(R.drawable.ic_undone_circle)
                    addTitleAndDoneButtonLayout.titleTextView.paintFlags = Paint.ANTI_ALIAS_FLAG
                }
            }

            collectLatestLifecycleFlow(viewModel.title) { title ->
                addTitleAndDoneButtonLayout.titleTextView.text = title
            }

            collectLatestLifecycleFlow(viewModel.description) { description ->
                addDescriptionLayout.descriptionTextView.text = description
            }

            collectLatestLifecycleFlow(viewModel.selectedStartTime) { selectedStartTime ->
                startTimeAndDateLayout.timeTextView.text = selectedStartTime.format(
                    DateTimeFormatter.ofPattern("HH:mm")
                )
            }
            collectLatestLifecycleFlow(viewModel.selectedStartDate) { selectedStartDate ->
                startTimeAndDateLayout.dateTextView.text = selectedStartDate.format(
                    DateTimeFormatter.ofPattern("MMM dd yyyy")
                )
            }

            collectLatestLifecycleFlow(viewModel.selectedReminderTime) { reminderTime ->
                when (reminderTime) {
                    ReminderTime.TEN_MINUTES_BEFORE ->
                        reminderLayout.reminderTextView.text =
                            getString(R.string.ten_minutes_before)
                    ReminderTime.THIRTY_MINUTES_BEFORE ->
                        reminderLayout.reminderTextView.text =
                            getString(R.string.thirty_minutes_before)
                    ReminderTime.ONE_HOUR_BEFORE ->
                        reminderLayout.reminderTextView.text =
                            getString(R.string.one_hour_before)
                    ReminderTime.SIX_HOURS_BEFORE ->
                        reminderLayout.reminderTextView.text =
                            getString(R.string.six_hours_before)
                    ReminderTime.ONE_DAY_BEFORE ->
                        reminderLayout.reminderTextView.text =
                            getString(R.string.one_day_before)
                }
            }
        }
    }

    private fun navigateToEditFragment(editType: EditType) {
        setFragmentResultListener("INPUT_REQUEST_KEY") { resultKey, bundle ->
            if (resultKey == "INPUT_REQUEST_KEY") {
                val input = bundle.getString("INPUT")
                when (editType) {
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
        when (editType) {
            EditType.TITLE -> {
                val text = binding.addTitleAndDoneButtonLayout.titleTextView.text.toString()
                val bundle = Bundle()
                bundle.putString("TEXT", text)
                bundle.putString("EDIT_TYPE", editType.name)

                setFragmentResult("EDIT_TYPE_AND_TEXT_REQUEST_KEY", bundle)

                navController.navigate(
                    ReminderDetailFragmentDirections
                        .actionReminderDetailFragmentToEditFragment()
                )
            }
            EditType.DESCRIPTION -> {
                val text = binding.addDescriptionLayout.descriptionTextView.text.toString()
                val bundle = Bundle()
                bundle.putString("TEXT", text)
                bundle.putString("EDIT_TYPE", editType.name)

                setFragmentResult("EDIT_TYPE_AND_TEXT_REQUEST_KEY", bundle)

                navController.navigate(
                    ReminderDetailFragmentDirections
                        .actionReminderDetailFragmentToEditFragment()
                )
            }
        }
    }

    private fun showTimePickerDialog(view: View) {
        val timePickerDialog = TimePickerDialog()
        val supportFragmentManager = requireActivity().supportFragmentManager

        supportFragmentManager.setFragmentResultListener(
            "REQUEST_KEY",
            viewLifecycleOwner
        ) { resultKey, bundle ->
            if (resultKey == "REQUEST_KEY") {
                val time = bundle.getString("SELECTED_TIME")
                val formatter = DateTimeFormatter.ofPattern("hh:mm a")
                if (view == binding.startTimeAndDateLayout.timeTextView ||
                    view == binding.startTimeAndDateLayout.timeButton
                ) {
                    viewModel.setStartTime(LocalTime.parse(time, formatter))
                }
            }
        }
        timePickerDialog.show(supportFragmentManager, "TimePickerDialog")
    }

    private fun showDatePickerDialog(view: View) {
        val datePickerFragment = DatePickerDialog()
        val supportFragmentManager = requireActivity().supportFragmentManager

        supportFragmentManager.setFragmentResultListener(
            "REQUEST_KEY",
            viewLifecycleOwner
        ) { resultKey, bundle ->
            if (resultKey == "REQUEST_KEY") {
                val date = bundle.getString("SELECTED_DATE")
                val formatter = DateTimeFormatter.ofPattern("MMM dd yyyy")

                if (view == binding.startTimeAndDateLayout.dateTextView ||
                    view == binding.startTimeAndDateLayout.dateButton
                ) {
                    viewModel.setStartDate(LocalDate.parse(date, formatter))
                }
            }
        }
        datePickerFragment.show(supportFragmentManager, "DatePickerFragment")
    }

    // View parameter determines which view popup appears under
    private fun showReminderOptionsPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.inflate(R.menu.menu_reminder_time_options)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.tenMinutes -> {
                    viewModel.setSelectedReminderTime(ReminderTime.TEN_MINUTES_BEFORE)
                    true
                }
                R.id.thirtyMinutes -> {
                    viewModel.setSelectedReminderTime(ReminderTime.THIRTY_MINUTES_BEFORE)
                    true
                }
                R.id.oneHour -> {
                    viewModel.setSelectedReminderTime(ReminderTime.ONE_HOUR_BEFORE)
                    true
                }
                R.id.sixHours -> {
                    viewModel.setSelectedReminderTime(ReminderTime.SIX_HOURS_BEFORE)
                    true
                }
                R.id.oneDay -> {
                    viewModel.setSelectedReminderTime(ReminderTime.ONE_DAY_BEFORE)
                    true
                }
                else -> true
            }
        }
        popupMenu.show()
    }

    private fun showDeleteConfirmationDialog() {
        val deleteConfirmationDialog = DeleteConfirmationDialog()
        val supportFragmentManager = requireActivity().supportFragmentManager

        supportFragmentManager.setFragmentResultListener(
            "REQUEST_KEY",
            viewLifecycleOwner
        ) { resultKey, bundle ->
            if (resultKey == "REQUEST_KEY") {
                val deleteAgendaItem = bundle.getBoolean("DELETE_AGENDA_ITEM")
                if (deleteAgendaItem) {
                    viewModel.deleteAgendaItem()
                    navController.popBackStack()
                }
            }
        }

        val bundle = Bundle()
        bundle.putString("AGENDA_ITEM_TYPE", agendaItemType.name)
        supportFragmentManager.setFragmentResult(
            "DELETE_CONFIRMATION_AGENDA_TYPE_REQUEST_KEY", bundle
        )

        deleteConfirmationDialog.show(supportFragmentManager, "DeleteConfirmationDialog")
    }
}
