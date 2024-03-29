package com.andrew.tasky.agenda.presentation.screens.event_detail

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrew.tasky.R
import com.andrew.tasky.agenda.domain.models.EventPhoto
import com.andrew.tasky.agenda.presentation.adapters.AttendeeItemAdapter
import com.andrew.tasky.agenda.presentation.adapters.PhotoItemAdapter
import com.andrew.tasky.agenda.util.*
import com.andrew.tasky.core.data.Resource
import com.andrew.tasky.databinding.FragmentEventDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

@AndroidEntryPoint
class EventDetailFragment : Fragment(R.layout.fragment_event_detail) {

    private val currentDate = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("dd MMMM yyyy")).uppercase()

    private val viewModel: EventDetailViewModel by hiltNavGraphViewModels(R.id.event_nav)
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
    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        Unit
    }
    private lateinit var photoAdapter: PhotoItemAdapter

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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    !areNotificationPermissionsEnabled(requireContext())
                ) {
                    notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                viewModel.saveEvent()
            }

            agendaItemTypeTVAndIconLayout.agendaItemIcon.setImageResource(R.drawable.ic_event_box)
            agendaItemTypeTVAndIconLayout.agendaItemTypeTextView.text = getString(R.string.event)

            addTitleAndDoneButtonLayout.taskDoneCircle.setOnClickListener {
                viewModel.setEditMode(true)
                viewModel.setIsDone(!viewModel.isDone.value)
            }

            addTitleAndDoneButtonLayout.titleTextView.setOnClickListener {
                viewModel.setEditMode(true)
                navigateToEditFragment(
                    editType = EditType.TITLE,
                    originalText = viewModel.title.value,
                    onResult = viewModel::setTitle
                )
            }
            addTitleAndDoneButtonLayout.editTitleButton.setOnClickListener {
                viewModel.setEditMode(true)
                navigateToEditFragment(
                    editType = EditType.TITLE,
                    originalText = viewModel.title.value,
                    onResult = viewModel::setTitle
                )
            }

            addDescriptionLayout.editDescriptionButton.setOnClickListener {
                viewModel.setEditMode(true)
                navigateToEditFragment(
                    editType = EditType.DESCRIPTION,
                    originalText = viewModel.description.value,
                    onResult = viewModel::setDescription
                )
            }
            addDescriptionLayout.descriptionTextView.setOnClickListener {
                viewModel.setEditMode(true)
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
                context = requireContext(),
                onPhotoClick = { photo -> openPhoto(photo) },
                onAddPhotoClick = { addPhoto() }
            )
            addPhotoLayout.photosRecyclerView.adapter = photoAdapter
            addPhotoLayout.photosRecyclerView.layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false
            )

            startTimeAndDateLayout.timeAndDateBeginningText.text = getString(R.string.from)

            startTimeAndDateLayout.timeTextView.setOnClickListener {
                viewModel.setEditMode(true)
                showTimePickerDialog(
                    onResult = viewModel::setStartTime,
                    initialTime = viewModel.selectedStartDateTime.value.toLocalTime()
                )
            }
            startTimeAndDateLayout.timeButton.setOnClickListener {
                viewModel.setEditMode(true)
                showTimePickerDialog(
                    onResult = viewModel::setStartTime,
                    initialTime = viewModel.selectedStartDateTime.value.toLocalTime()
                )
            }
            startTimeAndDateLayout.dateTextView.setOnClickListener {
                viewModel.setEditMode(true)
                showDatePickerDialog(
                    onResult = viewModel::setStartDate,
                    initialDate = viewModel.selectedStartDateTime.value.toLocalDate()
                )
            }
            startTimeAndDateLayout.dateButton.setOnClickListener {
                viewModel.setEditMode(true)
                showDatePickerDialog(
                    onResult = viewModel::setStartDate,
                    initialDate = viewModel.selectedStartDateTime.value.toLocalDate()
                )
            }

            endTimeAndDateLayout.timeAndDateBeginningText.text = getString(R.string.to)

            endTimeAndDateLayout.timeTextView.setOnClickListener {
                viewModel.setEditMode(true)
                showTimePickerDialog(
                    onResult = viewModel::setEndTime,
                    initialTime = viewModel.selectedEndDateTime.value.toLocalTime()
                )
            }
            endTimeAndDateLayout.timeButton.setOnClickListener {
                viewModel.setEditMode(true)
                showTimePickerDialog(
                    onResult = viewModel::setEndTime,
                    initialTime = viewModel.selectedEndDateTime.value.toLocalTime()
                )
            }
            endTimeAndDateLayout.dateTextView.setOnClickListener {
                viewModel.setEditMode(true)
                showDatePickerDialog(
                    onResult = viewModel::setEndDate,
                    initialDate = viewModel.selectedEndDateTime.value.toLocalDate()
                )
            }
            endTimeAndDateLayout.dateButton.setOnClickListener {
                viewModel.setEditMode(true)
                showDatePickerDialog(
                    onResult = viewModel::setEndDate,
                    initialDate = viewModel.selectedEndDateTime.value.toLocalDate()
                )
            }

            reminderLayout.reminderTextView.setOnClickListener {
                viewModel.setEditMode(true)
                showReminderOptionsPopupMenu(
                    it,
                    viewModel::setSelectedReminderTime
                )
            }
            reminderLayout.reminderButton.setOnClickListener {
                viewModel.setEditMode(true)
                showReminderOptionsPopupMenu(
                    it,
                    viewModel::setSelectedReminderTime
                )
            }

            attendeesLayout.addAttendeeButton.setOnClickListener {
                viewModel.setEditMode(true)
                showAttendeeDialog(
                    onEmailResult = viewModel::addAttendee,
                    onSuccessListener = attendeeSuccessChannel.receiveAsFlow()
                )
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
        }
    }

    private fun subscribeToObservables() {
        binding.apply {

            collectLatestLifecycleFlow(viewModel.isInEditMode) { isEditing ->
                header.saveButton.isVisible = isEditing
                header.saveButton.isEnabled = isEditing
                header.editButton.isVisible = !isEditing
                header.editButton.isEnabled = !isEditing

                reminderLayout.reminderTextView.isEnabled = isEditing
                reminderLayout.reminderButton.isEnabled = isEditing
                reminderLayout.reminderButton.isVisible = isEditing
            }

            collectLatestLifecycleFlow(viewModel.isCreatorEditing) { isCreatorEditing ->
                addTitleAndDoneButtonLayout.editTitleButton.isVisible = isCreatorEditing
                addTitleAndDoneButtonLayout.editTitleButton.isEnabled = isCreatorEditing
                addTitleAndDoneButtonLayout.titleTextView.isEnabled = isCreatorEditing

                addDescriptionLayout.editDescriptionButton.isVisible = isCreatorEditing
                addDescriptionLayout.editDescriptionButton.isEnabled = isCreatorEditing
                addDescriptionLayout.descriptionTextView.isEnabled = isCreatorEditing

                startTimeAndDateLayout.timeTextView.isEnabled = isCreatorEditing
                startTimeAndDateLayout.timeButton.isEnabled = isCreatorEditing
                startTimeAndDateLayout.timeButton.isVisible = isCreatorEditing
                startTimeAndDateLayout.dateTextView.isEnabled = isCreatorEditing
                startTimeAndDateLayout.dateButton.isEnabled = isCreatorEditing
                startTimeAndDateLayout.dateButton.isVisible = isCreatorEditing

                endTimeAndDateLayout.timeTextView.isEnabled = isCreatorEditing
                endTimeAndDateLayout.timeButton.isEnabled = isCreatorEditing
                endTimeAndDateLayout.timeButton.isVisible = isCreatorEditing
                endTimeAndDateLayout.dateTextView.isEnabled = isCreatorEditing
                endTimeAndDateLayout.dateButton.isEnabled = isCreatorEditing
                endTimeAndDateLayout.dateButton.isVisible = isCreatorEditing

                attendeesLayout.addAttendeeButton.isVisible = isCreatorEditing

                val goingAttendeeAdapter = AttendeeItemAdapter(
                    isCreatorEditing = isCreatorEditing,
                    onDeleteIconClick = viewModel::deleteAttendee
                )
                attendeesLayout.goingAttendeeRecyclerView.adapter = goingAttendeeAdapter
                attendeesLayout.goingAttendeeRecyclerView.layoutManager = LinearLayoutManager(
                    requireContext()
                )
                collectLatestLifecycleFlow(viewModel.goingAttendees) { goingAttendees ->
                    goingAttendeeAdapter.submitList(goingAttendees)
                }

                val notGoingAttendeeAdapter = AttendeeItemAdapter(
                    isCreatorEditing = isCreatorEditing,
                    onDeleteIconClick = viewModel::deleteAttendee
                )
                attendeesLayout.notGoingAttendeeRecyclerView.adapter = notGoingAttendeeAdapter
                attendeesLayout.notGoingAttendeeRecyclerView.layoutManager = LinearLayoutManager(
                    requireContext()
                )
                collectLatestLifecycleFlow(viewModel.notGoingAttendees) { notGoingAttendees ->
                    notGoingAttendeeAdapter.submitList(notGoingAttendees)
                }
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
            collectLatestLifecycleFlow(viewModel.isCreator) { isCreator ->
                if (isCreator) {
                    deleteBtn.deleteAgendaItemButton.text = String.format(
                        resources
                            .getString(R.string.delete_blank),
                        getString(R.string.event)
                    ).uppercase()
                    deleteBtn.deleteAgendaItemButton.setOnClickListener {
                        showDeleteConfirmationDialog(
                            getString(R.string.event).lowercase(),
                            onResultDeleteAgendaItem = {
                                viewModel.deleteEvent()
                                navController.popBackStack()
                            }
                        )
                    }
                } else {
                    collectLatestLifecycleFlow(viewModel.isAttendeeGoing) { isGoing ->
                        if (isGoing) {
                            deleteBtn.deleteAgendaItemButton.text = getString(R.string.leave_event)
                            deleteBtn.deleteAgendaItemButton.setOnClickListener {
                                viewModel.setEditMode(true)
                                viewModel.leaveEvent()
                            }
                        } else {
                            deleteBtn.deleteAgendaItemButton.text = getString(R.string.join_event)
                            deleteBtn.deleteAgendaItemButton.setOnClickListener {
                                viewModel.setEditMode(true)
                                viewModel.joinEvent()
                            }
                        }
                    }
                }
            }

            collectLatestLifecycleFlow(viewModel.finishedSavingEvent) {
                navController.popBackStack()
            }

            collectLatestLifecycleFlow(viewModel.uiEventPhotos) { photoList ->
                photoAdapter.submitList(photoList)

                addPhotoLayout.addPhotoPlusSign.isVisible = photoList.isEmpty()
                addPhotoLayout.addPhotoTextView.isVisible = photoList.isEmpty()
                addPhotoLayout.photosTextView.isVisible = photoList.isNotEmpty()
                addPhotoLayout.photosRecyclerView.isVisible = photoList.isNotEmpty()
            }

            collectLatestLifecycleFlow(viewModel.allowedToSeePhotoLayout) {
                allowedToSeePhotoLayout ->
                addPhotoLayout.addPhotoLayout.isVisible = allowedToSeePhotoLayout
            }

            collectLatestLifecycleFlow(viewModel.selectedStartDateTime) { selectedStartDateTime ->
                startTimeAndDateLayout.timeTextView.text = selectedStartDateTime.format(
                    DateTimeFormatter.ofPattern("HH:mm")
                )
                startTimeAndDateLayout.dateTextView.text = selectedStartDateTime.format(
                    DateTimeFormatter.ofPattern("MMM dd yyyy")
                )
            }

            collectLatestLifecycleFlow(viewModel.selectedEndDateTime) { selectedEndDateTime ->
                endTimeAndDateLayout.timeTextView.text = selectedEndDateTime.format(
                    DateTimeFormatter.ofPattern("HH:mm")
                )
                endTimeAndDateLayout.dateTextView.text = selectedEndDateTime.format(
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
            }
        }
        collectLatestLifecycleFlow(viewModel.isSavingEvent) { isSaving ->
            binding.progressBar.isVisible = isSaving
        }
        collectLatestLifecycleFlow(viewModel.addAttendee) { result ->
            when (result) {
                is Resource.Error -> {
                    Toast.makeText(
                        context,
                        result.message?.asString(requireContext()),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Success -> {
                    attendeeSuccessChannel.send(Unit)
                }
            }
        }
        collectLatestLifecycleFlow(viewModel.photosNotAddedToastMessage) {
            Toast.makeText(context, it.asString(requireContext()), Toast.LENGTH_LONG).show()
        }
    }

    private fun addPhoto() {
        addPhotoSearchForResult.launch("image/*")
    }
    private fun openPhoto(photo: EventPhoto) {
        viewModel.setPhotoOpened(photo)
        navController.navigate(
            EventDetailFragmentDirections.actionEventDetailFragmentToPhotoDetailFragment()
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun areNotificationPermissionsEnabled(context: Context): Boolean {
        return when (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
        ) {
            PackageManager.PERMISSION_GRANTED -> true
            else -> false
        }
    }

    private val attendeeSuccessChannel = Channel<Unit>()
}
