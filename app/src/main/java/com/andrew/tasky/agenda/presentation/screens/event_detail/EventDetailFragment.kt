package com.andrew.tasky.agenda.presentation.screens.event_detail

import android.graphics.Paint
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrew.tasky.R
import com.andrew.tasky.agenda.domain.models.Photo
import com.andrew.tasky.agenda.presentation.adapters.AttendeeItemAdapter
import com.andrew.tasky.agenda.presentation.adapters.PhotoItemAdapter
import com.andrew.tasky.agenda.presentation.dialogs.AddAttendeeDialog
import com.andrew.tasky.agenda.util.*
import com.andrew.tasky.databinding.FragmentEventDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
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
                viewModel.addPhoto(Photo(it.toString()))
            }
        }
    )
    private val isAttendee = false
    private val agendaItemType = AgendaItemType.EVENT

    private lateinit var photoAdapter: PhotoItemAdapter
    private lateinit var goingAttendeeAdapter: AttendeeItemAdapter
    private lateinit var notGoingAttendeeAdapter: AttendeeItemAdapter

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

            agendaItemTypeTVAndIconLayout.agendaItemIcon.setImageResource(R.drawable.ic_event_box)
            agendaItemTypeTVAndIconLayout.agendaItemTypeTextView.text = getString(R.string.event)

            addTitleAndDoneButtonLayout.taskDoneCircle.setOnClickListener {
                viewModel.setIsDone(!viewModel.isDone.value)
            }
            addTitleAndDoneButtonLayout.editTitleButton.isVisible = !isAttendee
            addTitleAndDoneButtonLayout.editTitleButton.isEnabled = !isAttendee
            addTitleAndDoneButtonLayout.titleTextView.isEnabled = !isAttendee
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

            addDescriptionLayout.editDescriptionButton.isVisible = !isAttendee
            addDescriptionLayout.editDescriptionButton.isEnabled = !isAttendee
            addDescriptionLayout.descriptionTextView.isEnabled = !isAttendee
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

            addPhotoLayout.addPhotoTextView.setOnClickListener {
                addPhoto()
            }
            addPhotoLayout.addPhotoPlusSign.setOnClickListener {
                addPhoto()
            }
            photoAdapter = PhotoItemAdapter(
                onPhotoClick = { index -> openPhoto(index) },
                onAddPhotoClick = { addPhoto() },
                userIsAttendee = isAttendee
            )
            addPhotoLayout.photosRecyclerView.adapter = photoAdapter
            addPhotoLayout.photosRecyclerView.layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false
            )

            startTimeAndDateLayout.timeAndDateBeginningText.text = getString(R.string.from)
            startTimeAndDateLayout.timeTextView.isEnabled = !isAttendee
            startTimeAndDateLayout.timeButton.isEnabled = !isAttendee
            startTimeAndDateLayout.timeButton.isVisible = !isAttendee
            startTimeAndDateLayout.dateTextView.isEnabled = !isAttendee
            startTimeAndDateLayout.dateButton.isEnabled = !isAttendee
            startTimeAndDateLayout.dateButton.isVisible = !isAttendee
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

            endTimeAndDateLayout.timeAndDateBeginningText.text = getString(R.string.to)
            endTimeAndDateLayout.timeTextView.isEnabled = !isAttendee
            endTimeAndDateLayout.timeButton.isEnabled = !isAttendee
            endTimeAndDateLayout.timeButton.isVisible = !isAttendee
            endTimeAndDateLayout.dateTextView.isEnabled = !isAttendee
            endTimeAndDateLayout.dateButton.isEnabled = !isAttendee
            endTimeAndDateLayout.dateButton.isVisible = !isAttendee
            endTimeAndDateLayout.timeTextView.setOnClickListener {
                showTimePickerDialog(viewModel::setEndTime)
            }
            endTimeAndDateLayout.timeButton.setOnClickListener {
                showTimePickerDialog(viewModel::setEndTime)
            }
            endTimeAndDateLayout.dateTextView.setOnClickListener {
                showDatePickerDialog(viewModel::setEndDate)
            }
            endTimeAndDateLayout.dateButton.setOnClickListener {
                showDatePickerDialog(viewModel::setEndDate)
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
            goingAttendeeAdapter = AttendeeItemAdapter(
                isAttendee,
                onDeleteIconClick = viewModel::deleteAttendee
            )
            attendeesLayout.goingAttendeeRecyclerView.adapter = goingAttendeeAdapter
            attendeesLayout.goingAttendeeRecyclerView.layoutManager = LinearLayoutManager(
                requireContext()
            )
            notGoingAttendeeAdapter = AttendeeItemAdapter(
                isAttendee,
                onDeleteIconClick = viewModel::deleteAttendee
            )
            attendeesLayout.notGoingAttendeeRecyclerView.adapter = notGoingAttendeeAdapter
            attendeesLayout.notGoingAttendeeRecyclerView.layoutManager = LinearLayoutManager(
                requireContext()
            )

            if (!isAttendee) {
                deleteBtn.deleteAgendaItemButton.text = String.format(
                    resources
                        .getString(R.string.delete_agenda_item_button),
                    getString(R.string.event)
                ).uppercase()
            }
            deleteBtn.deleteAgendaItemButton.setOnClickListener {
                if (isAttendee) {
                    viewModel.switchAttendingStatus()
                } else {
                    showDeleteConfirmationDialog(
                        agendaItemType,
                        onResultDeleteAgendaItem = {
                            viewModel.deleteAgendaItem()
                            navController.popBackStack()
                        }
                    )
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
                photoAdapter.submitList(photoList)

                addPhotoLayout.addPhotoLayout.isVisible = !isAttendee || photoList.isNotEmpty()
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
                    goingAttendeeAdapter.submitList(goingAttendees)
                }
                collectLatestLifecycleFlow(viewModel.notGoingAttendees) { notGoingAttendees ->
                    notGoingAttendeeAdapter.submitList(notGoingAttendees)
                }
            }
            if (isAttendee) {
                collectLatestLifecycleFlow(viewModel.isAttending) { isAttending ->
                    if (isAttending) {
                        deleteBtn.deleteAgendaItemButton.text = getString(R.string.join_event)
                    } else {
                        deleteBtn.deleteAgendaItemButton.text = getString(R.string.leave_event)
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

    private fun showAddAttendeeDialog() {
        val addAttendeeDialog = AddAttendeeDialog()
        val supportFragmentManager = requireActivity().supportFragmentManager

        addAttendeeDialog.show(supportFragmentManager, "AddAttendeeDialog")
    }
}
