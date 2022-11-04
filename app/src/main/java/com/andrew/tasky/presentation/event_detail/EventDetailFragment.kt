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
import com.andrew.tasky.databinding.CvReminderLayoutBinding
import com.andrew.tasky.databinding.CvTimeDateSelectorBinding
import com.andrew.tasky.databinding.FragmentEventDetailBinding
import com.andrew.tasky.domain.AgendaItem
import com.andrew.tasky.domain.AgendaItems
import com.andrew.tasky.domain.Attendee
import com.andrew.tasky.domain.AttendeeType
import com.andrew.tasky.presentation.adapters.AttendeeItemAdapter
import com.andrew.tasky.presentation.adapters.PhotoItemAdapter
import com.andrew.tasky.presentation.dialogs.AddAttendeeDialog
import com.andrew.tasky.presentation.dialogs.DatePickerDialog
import com.andrew.tasky.presentation.dialogs.DeleteConfirmationDialog
import com.andrew.tasky.presentation.dialogs.TimePickerDialog
import com.andrew.tasky.util.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class EventDetailFragment : Fragment(R.layout.fragment_event_detail) {

    private val currentDate = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("dd MMMM yyyy")).uppercase()

    private val viewModel: EventDetailViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var binding: FragmentEventDetailBinding
    private lateinit var startTimeDateBinding: CvTimeDateSelectorBinding
    private lateinit var endTimeDateBinding: CvTimeDateSelectorBinding
    private lateinit var reminderLayoutBinding: CvReminderLayoutBinding
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
        startTimeDateBinding = CvTimeDateSelectorBinding.bind(binding.startTimeAndDateLayout)
        endTimeDateBinding = CvTimeDateSelectorBinding.bind(binding.endTimeAndDateLayout)
        reminderLayoutBinding = CvReminderLayoutBinding.bind(binding.reminderLayout)
        navController = Navigation.findNavController(view)

        setupHeader()
        setupDoneButton()
        setupTitleLayout()
        setupDescriptionLayout()
        setupPhotoLayout()
        setupStartTimeLayout()
        setupEndTimeLayout()
        setupReminderTimesLayout()
        setupAttendeeLayout()
        setupDeleteJoinLeaveEventButton()

        val attendeesList = listOf(
            Attendee(
                name = "Samantha Jones", isAttending = true,
                attendeeType = AttendeeType.CREATOR, email = "random@gmail.com"
            ),
            Attendee(
                name = "Cappuccino Joe", isAttending = true,
                attendeeType = AttendeeType.ATTENDEE, email = "random1@gmail.com"
            ),
            Attendee(
                name = "Autumn Leaves", isAttending = true,
                attendeeType = AttendeeType.ATTENDEE, email = "random2@gmail.com"
            ),
            Attendee(
                name = "Andrew", isAttending = true,
                attendeeType = AttendeeType.ATTENDEE, email = "random3@gmail.com"
            ),
            Attendee(
                name = "Ramsay Beans", isAttending = false,
                attendeeType = AttendeeType.ATTENDEE, email = "random4@gmail.com"
            ),
            Attendee(
                name = "I Heart Lucy", isAttending = false,
                attendeeType = AttendeeType.ATTENDEE, email = "random5@gmail.com"
            ),
            Attendee(
                name = "I Have A Long name", isAttending = true,
                attendeeType = AttendeeType.ATTENDEE, email = "random6@gmail.com"
            )
        )

        for (name in attendeesList) {
            viewModel.addAttendee(name)
        }
    }

    private fun setupHeader() {
        binding.apply {

            currentDateTextView.text = currentDate

            collectLatestLifecycleFlow(viewModel.isInEditMode) { isEditing ->
                saveButton.isVisible = isEditing
                saveButton.isEnabled = isEditing
                editButton.isVisible = !isEditing
                editButton.isEnabled = !isEditing
            }

            closeButton.setOnClickListener {
                navController.popBackStack()
            }
            editButton.setOnClickListener {
                viewModel.setEditMode(true)
            }
            saveButton.setOnClickListener {
                saveEvent()
                navController.popBackStack()
            }
        }
    }

    private fun setupDoneButton() {
        binding.apply {
            collectLatestLifecycleFlow(viewModel.isDone) { isDone ->
                if (isDone) {
                    taskDoneCircle.setBackgroundResource(R.drawable.task_done_circle)
                    titleTextView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    taskDoneCircle.setBackgroundResource(R.drawable.ic_undone_circle)
                    binding.titleTextView.paintFlags = Paint.ANTI_ALIAS_FLAG
                }
            }
            taskDoneCircle.setOnClickListener {
                viewModel.setIsDone(!viewModel.isDone.value)
            }
        }
    }

    private fun setupTitleLayout() {
        binding.apply {

            collectLatestLifecycleFlow(viewModel.title) { title ->
                titleTextView.text = title
            }

            if (!isAttendee) {
                collectLatestLifecycleFlow(viewModel.isInEditMode) { isEditing ->
                    editTitleButton.isVisible = isEditing
                    editTitleButton.isEnabled = isEditing
                    titleTextView.isEnabled = isEditing
                }
                titleTextView.setOnClickListener {
                    navigateToEditFragment(EditType.TITLE)
                }
                editTitleButton.setOnClickListener {
                    navigateToEditFragment(EditType.TITLE)
                }
            } else {
                editTitleButton.isVisible = false
                editTitleButton.isEnabled = false
                titleTextView.isEnabled = false
            }
        }
    }

    private fun setupDescriptionLayout() {
        binding.apply {

            collectLatestLifecycleFlow(viewModel.description) { description ->
                descriptionTextView.text = description
            }

            if (!isAttendee) {
                collectLatestLifecycleFlow(viewModel.isInEditMode) { isEditing ->
                    editDescriptionButton.isVisible = isEditing
                    editDescriptionButton.isEnabled = isEditing
                    descriptionTextView.isEnabled = isEditing
                }
                editDescriptionButton.setOnClickListener {
                    navigateToEditFragment(EditType.DESCRIPTION)
                }
                descriptionTextView.setOnClickListener {
                    navigateToEditFragment(EditType.DESCRIPTION)
                }
            } else {
                editDescriptionButton.isVisible = false
                editDescriptionButton.isEnabled = false
                descriptionTextView.isEnabled = false
            }
        }
    }

    private fun setupPhotoLayout() {
        fun addPhoto() {
            addPhotoSearchForResult.launch("image/*")
        }

        fun openPhoto(index: Int) {
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

        binding.apply {
            collectLatestLifecycleFlow(viewModel.photos) { photoList ->
                addPhotoLayout.isVisible = !isAttendee || photoList.isNotEmpty()

                val adapter = PhotoItemAdapter(
                    photoList,
                    onPhotoClick = { index -> openPhoto(index) },
                    onAddPhotoClick = { addPhoto() },
                    userIsAttendee = isAttendee
                )
                photosRecyclerView.adapter = adapter
                photosRecyclerView.layoutManager = LinearLayoutManager(
                    requireContext(), LinearLayoutManager.HORIZONTAL, false
                )

                addPhotoPlusSign.isVisible = photoList.isEmpty()
                addPhotoTextView.isVisible = photoList.isEmpty()
                photosTextView.isVisible = photoList.isNotEmpty()
                photosRecyclerView.isVisible = photoList.isNotEmpty()
            }
            addPhotoTextView.setOnClickListener {
                addPhoto()
            }
            addPhotoPlusSign.setOnClickListener {
                addPhoto()
            }
        }
    }

    private fun setupStartTimeLayout() {
        startTimeDateBinding.apply {

            timeAndDateBeginningText.text = getString(R.string.from)

            collectLatestLifecycleFlow(viewModel.selectedStartTime) { selectedStartTime ->
                timeTextView.text = selectedStartTime.format(
                    DateTimeFormatter.ofPattern("HH:mm")
                )
            }
            collectLatestLifecycleFlow(viewModel.selectedStartDate) { selectedStartDate ->
                dateTextView.text = selectedStartDate.format(
                    DateTimeFormatter.ofPattern("MMM dd yyyy")
                )
            }

            if (!isAttendee) {
                collectLatestLifecycleFlow(viewModel.isInEditMode) { isEditing ->
                    timeTextView.isEnabled = isEditing
                    timeButton.isEnabled = isEditing
                    timeButton.isVisible = isEditing
                    dateTextView.isEnabled = isEditing
                    dateButton.isEnabled = isEditing
                    dateButton.isVisible = isEditing
                }

                timeTextView.setOnClickListener {
                    showTimePickerDialog(it)
                }
                timeButton.setOnClickListener {
                    showTimePickerDialog(it)
                }
                dateTextView.setOnClickListener {
                    showDatePickerDialog(it)
                }
                dateButton.setOnClickListener {
                    showDatePickerDialog(it)
                }
            } else {
                timeTextView.isEnabled = false
                timeButton.isEnabled = false
                timeButton.isVisible = false
                dateTextView.isEnabled = false
                dateButton.isEnabled = false
                dateButton.isVisible = false
            }
        }
    }

    private fun setupEndTimeLayout() {
        endTimeDateBinding.apply {

            timeAndDateBeginningText.text = getString(R.string.to)

            collectLatestLifecycleFlow(viewModel.selectedEndTime) { selectedEndTime ->
                timeTextView.text = selectedEndTime.format(
                    DateTimeFormatter.ofPattern("HH:mm")
                )
            }
            collectLatestLifecycleFlow(viewModel.selectedEndDate) { selectedEndDate ->
                dateTextView.text = selectedEndDate.format(
                    DateTimeFormatter.ofPattern("MMM dd yyyy")
                )
            }

            if (!isAttendee) {
                collectLatestLifecycleFlow(viewModel.isInEditMode) { isEditing ->
                    timeTextView.isEnabled = isEditing
                    timeButton.isEnabled = isEditing
                    timeButton.isVisible = isEditing
                    dateTextView.isEnabled = isEditing
                    dateButton.isEnabled = isEditing
                    dateButton.isVisible = isEditing
                }

                timeTextView.setOnClickListener {
                    showTimePickerDialog(it)
                }
                timeButton.setOnClickListener {
                    showTimePickerDialog(it)
                }
                dateTextView.setOnClickListener {
                    showDatePickerDialog(it)
                }
                dateButton.setOnClickListener {
                    showDatePickerDialog(it)
                }
            } else {
                timeTextView.isEnabled = false
                timeButton.isEnabled = false
                timeButton.isVisible = false
                dateTextView.isEnabled = false
                dateButton.isEnabled = false
                dateButton.isVisible = false
            }
        }
    }

    private fun setupReminderTimesLayout() {
        reminderLayoutBinding.apply {

            collectLatestLifecycleFlow(viewModel.isInEditMode) { isEditing ->
                reminderLayoutBinding.reminderTextView.isEnabled = isEditing
                reminderLayoutBinding.reminderButton.isEnabled = isEditing
                reminderLayoutBinding.reminderButton.isVisible = isEditing
            }
            collectLatestLifecycleFlow(viewModel.selectedReminderTime) { reminderTime ->
                when (reminderTime) {
                    ReminderTimes.TEN_MINUTES_BEFORE ->
                        reminderTextView.text =
                            getString(R.string.ten_minutes_before)
                    ReminderTimes.THIRTY_MINUTES_BEFORE ->
                        reminderTextView.text =
                            getString(R.string.thirty_minutes_before)
                    ReminderTimes.ONE_HOUR_BEFORE ->
                        reminderTextView.text =
                            getString(R.string.one_hour_before)
                    ReminderTimes.SIX_HOURS_BEFORE ->
                        reminderTextView.text =
                            getString(R.string.six_hours_before)
                    ReminderTimes.ONE_DAY_BEFORE ->
                        reminderTextView.text =
                            getString(R.string.one_day_before)
                }
            }

            reminderTextView.setOnClickListener {
                showReminderOptionsPopupMenu(it)
            }
            reminderButton.setOnClickListener {
                showReminderOptionsPopupMenu(it)
            }
        }
    }

    private fun setupAttendeeLayout() {
        binding.apply {

            addAttendeeButton.isVisible = !isAttendee
            addAttendeeButton.setOnClickListener {
                showAddAttendeeDialog()
            }

            collectLatestLifecycleFlow(viewModel.isInEditMode) { isEditing ->
                if (!isAttendee) {
                    addAttendeeButton.isVisible = isEditing
                }
            }
            collectLatestLifecycleFlow(viewModel.selectedAttendeeFilterType) { attendeeFilterType ->
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
            allButton.setOnClickListener {
                viewModel.setAttendeeFilterType(EventDetailViewModel.AttendeeFilterTypes.ALL)
            }
            goingButton.setOnClickListener {
                viewModel.setAttendeeFilterType(EventDetailViewModel.AttendeeFilterTypes.GOING)
            }
            notGoingButton.setOnClickListener {
                viewModel.setAttendeeFilterType(EventDetailViewModel.AttendeeFilterTypes.NOT_GOING)
            }
        }
        collectLatestLifecycleFlow(viewModel.goingAttendees) { goingAttendees ->
            val goingAttendeeAdapter = AttendeeItemAdapter(
                goingAttendees,
                isAttendee,
                onDeleteIconClick = { attendee -> viewModel.deleteAttendee(attendee) }
            )
            binding.goingAttendeeRecyclerView.adapter = goingAttendeeAdapter
        }
        binding.goingAttendeeRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        collectLatestLifecycleFlow(viewModel.notGoingAttendees) { notGoingAttendees ->
            val notGoingAttendeeAdapter = AttendeeItemAdapter(
                notGoingAttendees,
                isAttendee,
                onDeleteIconClick = { attendee -> viewModel.deleteAttendee(attendee) }
            )
            binding.notGoingAttendeeRecyclerView.adapter = notGoingAttendeeAdapter
        }
        binding.notGoingAttendeeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupDeleteJoinLeaveEventButton() {
        binding.apply {
            if (isAttendee) {
                collectLatestLifecycleFlow(viewModel.isAttending) { isAttending ->
                    if (isAttending) {
                        deleteAgendaItemButton.text = getString(R.string.join_event)
                    } else {
                        deleteAgendaItemButton.text = getString(R.string.leave_event)
                    }
                }
            } else {
                deleteAgendaItemButton.text = String.format(
                    resources
                        .getString(R.string.delete_agenda_item_button),
                    getString(R.string.event)
                ).uppercase()
            }

            deleteAgendaItemButton.setOnClickListener {
                if (isAttendee) {
                    viewModel.switchAttendingStatus()
                } else {
                    showDeleteConfirmationDialog()
                }
            }
        }
    }

    private fun saveEvent() {
        val newAgendaItem = AgendaItem(
            type = agendaItemType,
            isDone = viewModel.isDone.value,
            title = viewModel.title.value,
            description = viewModel.description.value,
            startDateAndTime = LocalDateTime.of(
                viewModel.selectedStartDate.value,
                viewModel.selectedStartTime.value
            ),
            endDateAndTime = LocalDateTime.of(
                viewModel.selectedEndDate.value,
                viewModel.selectedEndTime.value
            ),
            reminderTime = viewModel.selectedReminderTime.value,
            photos = viewModel.photos.value
        )
        if (args.agendaItem != null) {
            args.agendaItem?.let { oldAgendaItem ->
                AgendaItems.replaceAgendaItem(newAgendaItem, oldAgendaItem)
            }
        } else {
            AgendaItems.addAgendaItem(newAgendaItem)
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
                val text = binding.titleTextView.text.toString()
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
                val text = binding.descriptionTextView.text.toString()
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
                if (view == startTimeDateBinding.timeTextView ||
                    view == startTimeDateBinding.timeButton
                ) {
                    viewModel.setStartTime(LocalTime.parse(time, formatter))
                } else if (view == endTimeDateBinding.timeTextView ||
                    view == endTimeDateBinding.timeButton
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

                if (view == startTimeDateBinding.dateTextView ||
                    view == startTimeDateBinding.dateButton
                ) {
                    viewModel.setStartDate(LocalDate.parse(date, formatter))
                } else if (view == endTimeDateBinding.dateTextView ||
                    view == endTimeDateBinding.dateButton
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
        reminderLayoutBinding.reminderTextView.apply {
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
                    args.agendaItem?.let {
                        AgendaItems.deleteAgendaItem(it)
                    }
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
