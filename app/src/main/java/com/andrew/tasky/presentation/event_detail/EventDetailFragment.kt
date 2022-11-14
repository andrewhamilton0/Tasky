package com.andrew.tasky.presentation.event_detail

import android.graphics.Paint
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrew.tasky.R
import com.andrew.tasky.databinding.FragmentEventDetailBinding
import com.andrew.tasky.presentation.adapters.AttendeeItemAdapter
import com.andrew.tasky.presentation.adapters.PhotoItemAdapter
import com.andrew.tasky.presentation.dialogs.AddAttendeeDialog
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
class EventDetailFragment : Fragment(R.layout.fragment_event_detail) {

    private val currentDate = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("dd MMMM yyyy")).uppercase()

    private val viewModel: EventDetailViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var binding: FragmentEventDetailBinding
    private val addPhotoSearchForResult = registerForActivityResult(
        ActivityResultContracts.GetContent(),
        ActivityResultCallback {
            if (it != null) {
                viewModel.addPhoto(it)
            }
        }
    )
    private val args: EventDetailFragmentArgs by navArgs()
    private val isAttendee = false
    private val agendaItemType = AgendaItemType.EVENT

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEventDetailBinding.bind(view)
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

            addTitleAndDoneButtonLayout.taskDoneCircle.setOnClickListener {
                viewModel.setIsDone(!viewModel.isDone.value)
            }
            addTitleAndDoneButtonLayout.editTitleButton.isVisible = !isAttendee
            addTitleAndDoneButtonLayout.editTitleButton.isEnabled = !isAttendee
            addTitleAndDoneButtonLayout.titleTextView.isEnabled = !isAttendee
            addTitleAndDoneButtonLayout.titleTextView.setOnClickListener {
                navigateToEditFragment(EditType.TITLE)
            }
            addTitleAndDoneButtonLayout.editTitleButton.setOnClickListener {
                navigateToEditFragment(EditType.TITLE)
            }

            addDescriptionLayout.editDescriptionButton.isVisible = !isAttendee
            addDescriptionLayout.editDescriptionButton.isEnabled = !isAttendee
            addDescriptionLayout.descriptionTextView.isEnabled = !isAttendee
            addDescriptionLayout.editDescriptionButton.setOnClickListener {
                navigateToEditFragment(EditType.DESCRIPTION)
            }
            addDescriptionLayout.descriptionTextView.setOnClickListener {
                navigateToEditFragment(EditType.DESCRIPTION)
            }

            addPhotoLayout.addPhotoTextView.setOnClickListener {
                addPhoto()
            }
            addPhotoLayout.addPhotoPlusSign.setOnClickListener {
                addPhoto()
            }

            startTimeAndDateLayout.timeAndDateBeginningText.text = getString(R.string.from)
            startTimeAndDateLayout.timeTextView.isEnabled = !isAttendee
            startTimeAndDateLayout.timeButton.isEnabled = !isAttendee
            startTimeAndDateLayout.timeButton.isVisible = !isAttendee
            startTimeAndDateLayout.dateTextView.isEnabled = !isAttendee
            startTimeAndDateLayout.dateButton.isEnabled = !isAttendee
            startTimeAndDateLayout.dateButton.isVisible = !isAttendee
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

            endTimeAndDateLayout.timeAndDateBeginningText.text = getString(R.string.to)
            endTimeAndDateLayout.timeTextView.isEnabled = !isAttendee
            endTimeAndDateLayout.timeButton.isEnabled = !isAttendee
            endTimeAndDateLayout.timeButton.isVisible = !isAttendee
            endTimeAndDateLayout.dateTextView.isEnabled = !isAttendee
            endTimeAndDateLayout.dateButton.isEnabled = !isAttendee
            endTimeAndDateLayout.dateButton.isVisible = !isAttendee
            endTimeAndDateLayout.timeTextView.setOnClickListener {
                showTimePickerDialog(it)
            }
            endTimeAndDateLayout.timeButton.setOnClickListener {
                showTimePickerDialog(it)
            }
            endTimeAndDateLayout.dateTextView.setOnClickListener {
                showDatePickerDialog(it)
            }
            endTimeAndDateLayout.dateButton.setOnClickListener {
                showDatePickerDialog(it)
            }

            reminderLayout.reminderTextView.setOnClickListener {
                showReminderOptionsPopupMenu(it)
            }
            reminderLayout.reminderButton.setOnClickListener {
                showReminderOptionsPopupMenu(it)
            }

            attendeesLayout.addAttendeeButton.isVisible = !isAttendee
            attendeesLayout.addAttendeeButton.setOnClickListener {
                showAddAttendeeDialog()
            }
            attendeesLayout.allButton.setOnClickListener {
                viewModel.setAttendeeFilterType(EventDetailViewModel.AttendeeFilterTypes.ALL)
            }
            attendeesLayout.goingButton.setOnClickListener {
                viewModel.setAttendeeFilterType(EventDetailViewModel.AttendeeFilterTypes.GOING)
            }
            attendeesLayout.notGoingButton.setOnClickListener {
                viewModel.setAttendeeFilterType(EventDetailViewModel.AttendeeFilterTypes.NOT_GOING)
            }

            if (!isAttendee) {
                btmActionTvBtn.deleteAgendaItemButton.text = String.format(
                    resources
                        .getString(R.string.delete_agenda_item_button),
                    getString(R.string.event)
                ).uppercase()
            }
            btmActionTvBtn.deleteAgendaItemButton.setOnClickListener {
                if (isAttendee) {
                    viewModel.switchAttendingStatus()
                } else {
                    showDeleteConfirmationDialog()
                }
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

                if (!isAttendee) {
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

                    endTimeAndDateLayout.timeTextView.isEnabled = isEditing
                    endTimeAndDateLayout.timeButton.isEnabled = isEditing
                    endTimeAndDateLayout.timeButton.isVisible = isEditing
                    endTimeAndDateLayout.dateTextView.isEnabled = isEditing
                    endTimeAndDateLayout.dateButton.isEnabled = isEditing
                    endTimeAndDateLayout.dateButton.isVisible = isEditing

                    attendeesLayout.addAttendeeButton.isVisible = isEditing
                }

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

            collectLatestLifecycleFlow(viewModel.photos) { photoList ->
                addPhotoLayout.addPhotoLayout.isVisible = !isAttendee || photoList.isNotEmpty()

                val adapter = PhotoItemAdapter(
                    photoList,
                    onPhotoClick = { index -> openPhoto(index) },
                    onAddPhotoClick = { addPhoto() },
                    userIsAttendee = isAttendee
                )
                addPhotoLayout.photosRecyclerView.adapter = adapter
                addPhotoLayout.photosRecyclerView.layoutManager = LinearLayoutManager(
                    requireContext(), LinearLayoutManager.HORIZONTAL, false
                )

                addPhotoLayout.addPhotoPlusSign.isVisible = photoList.isEmpty()
                addPhotoLayout.addPhotoTextView.isVisible = photoList.isEmpty()
                addPhotoLayout.photosTextView.isVisible = photoList.isNotEmpty()
                addPhotoLayout.photosRecyclerView.isVisible = photoList.isNotEmpty()
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

            collectLatestLifecycleFlow(viewModel.selectedEndTime) { selectedEndTime ->
                endTimeAndDateLayout.timeTextView.text = selectedEndTime.format(
                    DateTimeFormatter.ofPattern("HH:mm")
                )
            }
            collectLatestLifecycleFlow(viewModel.selectedEndDate) { selectedEndDate ->
                endTimeAndDateLayout.dateTextView.text = selectedEndDate.format(
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

            attendeesLayout.apply {
                collectLatestLifecycleFlow(
                    viewModel.selectedAttendeeFilterType
                ) { attendeeFilterType ->
                    when (attendeeFilterType) {
                        EventDetailViewModel.AttendeeFilterTypes.ALL -> {
                            goingTextView.isVisible = true
                            goingAttendeeRecyclerView.isVisible = true
                            notGoingTextView.isVisible = true
                            notGoingAttendeeRecyclerView.isVisible = true
                            allButtonTextView.setTextColor(
                                ResourcesCompat
                                    .getColor(resources, R.color.white, null)
                            )
                            allButton.background.setTint(
                                (
                                    ResourcesCompat
                                        .getColor(resources, R.color.black, null)
                                    )
                            )
                            goingButtonTextView.setTextColor(
                                ResourcesCompat
                                    .getColor(resources, R.color.dark_gray, null)
                            )
                            goingButton.background.setTint(
                                ResourcesCompat
                                    .getColor(resources, R.color.light_2, null)
                            )
                            notGoingButtonTextView.setTextColor(
                                ResourcesCompat
                                    .getColor(resources, R.color.dark_gray, null)
                            )
                            notGoingButton.background.setTint(
                                ResourcesCompat
                                    .getColor(resources, R.color.light_2, null)
                            )
                        }
                        EventDetailViewModel.AttendeeFilterTypes.GOING -> {
                            goingTextView.isVisible = true
                            goingAttendeeRecyclerView.isVisible = true
                            notGoingTextView.isVisible = false
                            notGoingAttendeeRecyclerView.isVisible = false
                            allButtonTextView.setTextColor(
                                ResourcesCompat
                                    .getColor(resources, R.color.dark_gray, null)
                            )
                            allButton.background.setTint(
                                ResourcesCompat
                                    .getColor(resources, R.color.light_2, null)
                            )
                            goingButtonTextView.setTextColor(
                                ResourcesCompat
                                    .getColor(resources, R.color.white, null)
                            )
                            goingButton.background.setTint(
                                ResourcesCompat
                                    .getColor(resources, R.color.black, null)
                            )
                            notGoingButtonTextView.setTextColor(
                                ResourcesCompat
                                    .getColor(resources, R.color.dark_gray, null)
                            )
                            notGoingButton.background.setTint(
                                ResourcesCompat
                                    .getColor(resources, R.color.light_2, null)
                            )
                        }
                        EventDetailViewModel.AttendeeFilterTypes.NOT_GOING -> {
                            goingTextView.isVisible = false
                            goingAttendeeRecyclerView.isVisible = false
                            notGoingTextView.isVisible = true
                            notGoingAttendeeRecyclerView.isVisible = true
                            allButtonTextView.setTextColor(
                                ResourcesCompat
                                    .getColor(resources, R.color.dark_gray, null)
                            )
                            allButton.background.setTint(
                                ResourcesCompat
                                    .getColor(resources, R.color.light_2, null)
                            )
                            goingButtonTextView.setTextColor(
                                ResourcesCompat
                                    .getColor(resources, R.color.dark_gray, null)
                            )
                            goingButton.background.setTint(
                                ResourcesCompat
                                    .getColor(resources, R.color.light_2, null)
                            )
                            notGoingButtonTextView.setTextColor(
                                ResourcesCompat
                                    .getColor(resources, R.color.white, null)
                            )
                            notGoingButton.background.setTint(
                                ResourcesCompat
                                    .getColor(resources, R.color.black, null)
                            )
                        }
                    }
                }
                collectLatestLifecycleFlow(viewModel.goingAttendees) { goingAttendees ->
                    val goingAttendeeAdapter = AttendeeItemAdapter(
                        goingAttendees,
                        isAttendee,
                        onDeleteIconClick = { attendee -> viewModel.deleteAttendee(attendee) }
                    )
                    goingAttendeeRecyclerView.adapter = goingAttendeeAdapter
                    goingAttendeeRecyclerView.layoutManager = LinearLayoutManager(
                        requireContext()
                    )
                }
                collectLatestLifecycleFlow(viewModel.notGoingAttendees) { notGoingAttendees ->
                    val notGoingAttendeeAdapter = AttendeeItemAdapter(
                        notGoingAttendees,
                        isAttendee,
                        onDeleteIconClick = { attendee -> viewModel.deleteAttendee(attendee) }
                    )
                    notGoingAttendeeRecyclerView.adapter = notGoingAttendeeAdapter
                    notGoingAttendeeRecyclerView.layoutManager = LinearLayoutManager(
                        requireContext()
                    )
                }
            }
            if (isAttendee) {
                collectLatestLifecycleFlow(viewModel.isAttending) { isAttending ->
                    if (isAttending) {
                        btmActionTvBtn.deleteAgendaItemButton.text = getString(R.string.join_event)
                    } else {
                        btmActionTvBtn.deleteAgendaItemButton.text = getString(R.string.leave_event)
                    }
                }
            }
        }
    }

    private fun addPhoto() {
        addPhotoSearchForResult.launch("image/*")
    }
    private fun openPhoto(index: Int) {
        setFragmentResultListener("REQUEST_KEY") { resultKey, bundle ->
            if (resultKey == "REQUEST_KEY") {
                val photoToDelete = bundle.getInt("DELETE_PHOTO_INDEX")
                viewModel.deletePhoto(photoToDelete)
            }
        }
        navController.navigate(
            EventDetailFragmentDirections
                .actionEventDetailFragmentToPhotoDetailFragment(
                    viewModel.photos.value[index].toString(), index
                )
        )
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
                    EventDetailFragmentDirections
                        .actionEventDetailFragmentToEditFragment()
                )
            }
            EditType.DESCRIPTION -> {
                val text = binding.addDescriptionLayout.descriptionTextView.text.toString()
                val bundle = Bundle()
                bundle.putString("TEXT", text)
                bundle.putString("EDIT_TYPE", editType.name)

                setFragmentResult("EDIT_TYPE_AND_TEXT_REQUEST_KEY", bundle)

                navController.navigate(
                    EventDetailFragmentDirections
                        .actionEventDetailFragmentToEditFragment()
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
                } else if (view == binding.endTimeAndDateLayout.timeTextView ||
                    view == binding.endTimeAndDateLayout.timeButton
                ) {
                    viewModel.setEndTime(LocalTime.parse(time, formatter))
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
                } else if (view == binding.endTimeAndDateLayout.dateTextView ||
                    view == binding.endTimeAndDateLayout.dateButton
                ) {
                    viewModel.setEndDate(LocalDate.parse(date, formatter))
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

    private fun showAddAttendeeDialog() {
        val addAttendeeDialog = AddAttendeeDialog()
        val supportFragmentManager = requireActivity().supportFragmentManager

        addAttendeeDialog.show(supportFragmentManager, "AddAttendeeDialog")
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
