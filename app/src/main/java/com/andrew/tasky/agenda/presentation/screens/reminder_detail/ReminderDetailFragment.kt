package com.andrew.tasky.agenda.presentation.screens.reminder_detail

import android.graphics.Paint
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.andrew.tasky.R
import com.andrew.tasky.agenda.util.*
import com.andrew.tasky.databinding.FragmentReminderDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class ReminderDetailFragment : Fragment(R.layout.fragment_reminder_detail) {

    private val currentDate = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("dd MMMM yyyy")).uppercase()

    private val viewModel: ReminderDetailViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var binding: FragmentReminderDetailBinding

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
                navigateToEditFragment(
                    editType = EditType.TITLE,
                    originalText = viewModel.title.value,
                    onResult = viewModel::setTitle
                )
            }
            addTitleAndDoneButtonLayout.editTitleButton.setOnClickListener {
                navigateToEditFragment(
                    editType = EditType.TITLE,
                    originalText = viewModel.title.value,
                    onResult = viewModel::setTitle
                )
            }

            addDescriptionLayout.editDescriptionButton.setOnClickListener {
                navigateToEditFragment(
                    editType = EditType.DESCRIPTION,
                    originalText = viewModel.description.value,
                    onResult = viewModel::setDescription
                )
            }
            addDescriptionLayout.descriptionTextView.setOnClickListener {
                navigateToEditFragment(
                    editType = EditType.DESCRIPTION,
                    originalText = viewModel.description.value,
                    onResult = viewModel::setDescription
                )
            }

            startTimeAndDateLayout.timeAndDateBeginningText.text = getString(R.string.from)
            startTimeAndDateLayout.timeTextView.setOnClickListener {
                showTimePickerDialog(viewModel::setStartTime)
            }
            startTimeAndDateLayout.timeButton.setOnClickListener {
                showTimePickerDialog(viewModel::setStartTime)
            }
            startTimeAndDateLayout.dateTextView.setOnClickListener {
                showDatePickerDialog(viewModel::setStartDate)
            }
            startTimeAndDateLayout.dateButton.setOnClickListener {
                showDatePickerDialog(viewModel::setStartDate)
            }

            reminderLayout.reminderTextView.setOnClickListener {
                showReminderOptionsPopupMenu(
                    it,
                    viewModel::setSelectedReminderTime
                )
            }
            reminderLayout.reminderButton.setOnClickListener {
                showReminderOptionsPopupMenu(
                    it,
                    viewModel::setSelectedReminderTime
                )
            }
            deleteBtn.deleteAgendaItemButton.text = getString(
                R.string.delete_blank,
                getString(R.string.reminder)
            ).uppercase()
            deleteBtn.deleteAgendaItemButton.setOnClickListener {
                showDeleteConfirmationDialog(
                    getString(R.string.reminder).lowercase(),
                    onResultDeleteAgendaItem = {
                        viewModel.deleteAgendaItem()
                        navController.popBackStack()
                    }
                )
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
}
