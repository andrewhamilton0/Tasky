package com.andrew.tasky.presentation.agenda_item_detail

import android.graphics.Paint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrew.tasky.R
import com.andrew.tasky.databinding.FragmentAgendaItemDetailBinding
import com.andrew.tasky.domain.AgendaItem
import com.andrew.tasky.domain.AgendaItems
import com.andrew.tasky.domain.Attendee
import com.andrew.tasky.domain.AttendeeType
import com.andrew.tasky.presentation.adapters.AttendeeItemAdapter
import com.andrew.tasky.presentation.adapters.PhotoItemAdapter
import com.andrew.tasky.presentation.dialogs.DatePickerFragment
import com.andrew.tasky.presentation.dialogs.DeleteConfirmationDialogFragment
import com.andrew.tasky.presentation.dialogs.TimePickerFragment
import com.andrew.tasky.util.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AgendaItemDetailFragment: Fragment(R.layout.fragment_agenda_item_detail), AgendaItemDetailFragmentCommunicationWithRV {

    private val isAttendee = false

    private val currentDate = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("dd MMMM yyyy")).uppercase()

    private lateinit var viewModel: AgendaItemDetailViewModel
    private lateinit var navController: NavController
    private lateinit var binding: FragmentAgendaItemDetailBinding
    private lateinit var agendaItemType: AgendaItemType
    private val addPhotoSearchForResult = registerForActivityResult(
        ActivityResultContracts.GetContent(),
        ActivityResultCallback {
            if (it != null) {
                viewModel.addPhoto(it)
            }
        }
    )
    private val args: AgendaItemDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAgendaItemDetailBinding.bind(view)
        navController = Navigation.findNavController(view)
        viewModel = ViewModelProvider(this)[AgendaItemDetailViewModel::class.java]
        agendaItemType = args.agendaItemType

        subscribeToObservables()
        setupAgendaInitialViewModels()
        setupAgendaItemType()
        onClickListeners()

        val attendeesList = listOf(
            Attendee(name = "Samantha Jones", isAttending = true, attendeeType = AttendeeType.CREATOR),
            Attendee(name = "Cappuccino Joe", isAttending = true, attendeeType = AttendeeType.ATTENDEE),
            Attendee(name = "Autumn Leaves", isAttending = true, attendeeType = AttendeeType.ATTENDEE),
            Attendee(name = "Andrew", isAttending = true, attendeeType = AttendeeType.ATTENDEE),
            Attendee(name = "Ramsay Beans", isAttending = false, attendeeType = AttendeeType.ATTENDEE),
            Attendee(name = "I Heart Lucy", isAttending = false, attendeeType = AttendeeType.ATTENDEE),
            Attendee(name = "I Have A Long name", isAttending = true, attendeeType = AttendeeType.ATTENDEE)
        )

        for (name in attendeesList){
            viewModel.addAttendee(name)
        }
    }

    private fun subscribeToObservables() {
        binding.apply {
            collectLatestLifecycleFlow(viewModel.isInEditMode) { editMode ->
                setEditMode(editMode)
            }
            collectLatestLifecycleFlow(viewModel.isDone) { isDone ->
                if (isDone) {
                    taskDoneCircle.setBackgroundResource(R.drawable.task_done_circle)
                    agendaItemTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    taskDoneCircle.setBackgroundResource(R.drawable.ic_undone_circle)
                    binding.agendaItemTitle.paintFlags = Paint.ANTI_ALIAS_FLAG
                }
            }
            collectLatestLifecycleFlow(viewModel.title) { title ->
                agendaItemTitle.text = title
            }
            collectLatestLifecycleFlow(viewModel.description) { description ->
                descriptionTextView.text = description
            }
            collectLatestLifecycleFlow(viewModel.selectedStartTime) { selectedStartTime ->
                startTime.text = selectedStartTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            }
            collectLatestLifecycleFlow(viewModel.selectedStartDate) { selectedStartDate ->
                startDate.text = selectedStartDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy"))
            }
            collectLatestLifecycleFlow(viewModel.selectedEndTime) { selectedEndTime ->
                endTime.text = selectedEndTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            }
            collectLatestLifecycleFlow(viewModel.selectedEndDate) { selectedEndDate ->
                endDate.text = selectedEndDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy"))
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
            collectLatestLifecycleFlow(viewModel.selectedAttendeeButton){
                when(it){
                    AgendaItemDetailViewModel.AttendeeButtonTypes.ALL -> {
                        goingTextView.isVisible = true
                        goingAttendeeRecyclerView.isVisible = true
                        notGoingTextView.isVisible = true
                        notGoingAttendeeRecyclerView.isVisible = true
                        allButtonTextView.setTextColor(ResourcesCompat
                            .getColor(resources, R.color.white, null))
                        allButton.background.setTint((ResourcesCompat
                            .getColor(resources, R.color.black, null)))
                        goingButtonTextView.setTextColor(ResourcesCompat
                            .getColor(resources, R.color.dark_gray, null))
                        goingButton.background.setTint(ResourcesCompat
                            .getColor(resources, R.color.light_2, null))
                        notGoingButtonTextView.setTextColor(ResourcesCompat
                            .getColor(resources, R.color.dark_gray, null))
                        notGoingButton.background.setTint(ResourcesCompat
                            .getColor(resources, R.color.light_2, null))
                    }
                    AgendaItemDetailViewModel.AttendeeButtonTypes.GOING -> {
                        goingTextView.isVisible = true
                        goingAttendeeRecyclerView.isVisible = true
                        notGoingTextView.isVisible = false
                        notGoingAttendeeRecyclerView.isVisible = false
                        allButtonTextView.setTextColor(ResourcesCompat
                            .getColor(resources, R.color.dark_gray, null))
                        allButton.background.setTint(ResourcesCompat
                            .getColor(resources, R.color.light_2, null))
                        goingButtonTextView.setTextColor(ResourcesCompat
                            .getColor(resources, R.color.white, null))
                        goingButton.background.setTint(ResourcesCompat
                            .getColor(resources, R.color.black, null))
                        notGoingButtonTextView.setTextColor(ResourcesCompat
                            .getColor(resources, R.color.dark_gray, null))
                        notGoingButton.background.setTint(ResourcesCompat
                            .getColor(resources, R.color.light_2, null))
                    }
                    AgendaItemDetailViewModel.AttendeeButtonTypes.NOT_GOING -> {
                        goingTextView.isVisible = false
                        goingAttendeeRecyclerView.isVisible = false
                        notGoingTextView.isVisible = true
                        notGoingAttendeeRecyclerView.isVisible = true
                        allButtonTextView.setTextColor(ResourcesCompat
                            .getColor(resources, R.color.dark_gray, null))
                        allButton.background.setTint(ResourcesCompat
                            .getColor(resources, R.color.light_2, null))
                        goingButtonTextView.setTextColor(ResourcesCompat
                            .getColor(resources, R.color.dark_gray, null))
                        goingButton.background.setTint(ResourcesCompat
                            .getColor(resources, R.color.light_2, null))
                        notGoingButtonTextView.setTextColor(ResourcesCompat
                            .getColor(resources, R.color.white, null))
                        notGoingButton.background.setTint(ResourcesCompat
                            .getColor(resources, R.color.black, null))

                    }
                }
            }
            if(isAttendee){
                collectLatestLifecycleFlow(viewModel.isAttending){isAttending ->
                    if(isAttending){
                        deleteAgendaItemButton.text = getString(R.string.join_event)
                    }
                    else{
                        deleteAgendaItemButton.text = getString(R.string.leave_event)
                    }
                }
            }
        }
        collectLatestLifecycleFlow(viewModel.photos){ photoList ->
            val adapter = PhotoItemAdapter(photoList, this, isAttendee)
            binding.photosRecyclerView.adapter = adapter
            binding.photosRecyclerView.layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false)

                //If there are photos, shows the recyclerView and makes original add photo disappear
                binding.addPhotoPlusSign.isVisible = photoList.isEmpty()
                binding.addPhotoTextView.isVisible = photoList.isEmpty()
                binding.photosTextView.isVisible = photoList.isNotEmpty()
                binding.photosRecyclerView.isVisible = photoList.isNotEmpty()
        }
        collectLatestLifecycleFlow(viewModel.goingAttendees) { goingAttendees ->
            val goingAttendeeAdapter = AttendeeItemAdapter(goingAttendees, this)
            binding.goingAttendeeRecyclerView.adapter = goingAttendeeAdapter
            binding.goingAttendeeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
        collectLatestLifecycleFlow(viewModel.notGoingAttendees){ notGoingAttendees ->
            val notGoingAttendeeAdapter = AttendeeItemAdapter(notGoingAttendees, this)
            binding.notGoingAttendeeRecyclerView.adapter = notGoingAttendeeAdapter
            binding.notGoingAttendeeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupAttendeeAndCreatorView(){
        binding.apply {
            if(isAttendee){
                addAttendeeButton.isVisible = false
                if(viewModel.photos.value.isEmpty()){
                    addPhotoLayout.isVisible = false
                }
            }
        }
    }

    private fun setupAgendaInitialViewModels() {
        if (!viewModel.isInitiallySetup.value) {
            args.agendaItem?.let {
                viewModel.setTitle(it.title)
                viewModel.setDescription(it.description)
                viewModel.setStartDate(it.startDateAndTime.toLocalDate())
                viewModel.setStartTime(it.startDateAndTime.toLocalTime())
                viewModel.setIsDone(it.isDone)
                viewModel.setSelectedReminderTime(it.reminderTime)
                it.photos?.let { photoList -> viewModel.setupPhotos(photoList) }
            }
            viewModel.setEditMode(args.isInEditMode)
            viewModel.setInitialSetupToTrue()
        }
    }

    private fun setupAgendaItemType(){
        binding.apply {
            when (agendaItemType) {
                AgendaItemType.TASK -> {
                    taskColorBox.setImageResource(R.drawable.ic_task_box)
                    agendaItemTypeTextView.text = getString(R.string.task)
                    addPhotoLayout.isVisible = false
                    startTimeAndDateBeginningText.text = getString(R.string.at)
                    endTimeAndDateLayout.isVisible = false
                    attendeesLayout.isVisible = false
                    deleteAgendaItemButton.text = String.format(resources.
                    getString(R.string.delete_agenda_item_button), getString(R.string.task)).uppercase()
                }
                AgendaItemType.EVENT -> {
                    taskColorBox.setImageResource(R.drawable.ic_event_box)
                    agendaItemTypeTextView.text = getString(R.string.event)
                    addPhotoLayout.isVisible = true
                    startTimeAndDateBeginningText.text = getString(R.string.from)
                    endTimeAndDateLayout.isVisible = true
                    attendeesLayout.isVisible = true
                    deleteAgendaItemButton.text = String.format(resources.
                    getString(R.string.delete_agenda_item_button), getString(R.string.event)).uppercase()
                    setupAttendeeAndCreatorView()
                }
                AgendaItemType.REMINDER -> {
                    taskColorBox.setImageResource(R.drawable.ic_reminder_box)
                    agendaItemTypeTextView.text = getString(R.string.reminder)
                    addPhotoLayout.isVisible = false
                    startTimeAndDateBeginningText.text = getString(R.string.from)
                    endTimeAndDateLayout.isVisible = false
                    attendeesLayout.isVisible = false
                    deleteAgendaItemButton.text = String.format(resources.
                    getString(R.string.delete_agenda_item_button), getString(R.string.reminder)).uppercase()
                }
            }
        }
    }

    override fun addNewPhoto() {
        super.addNewPhoto()
        addPhotoSearchForResult.launch("image/*")
    }

    override fun openPhoto(index: Int) {
        super.openPhoto(index)

        //Listener to Delete Image
        setFragmentResultListener("REQUEST_KEY"){
            resultKey, bundle -> if(resultKey == "REQUEST_KEY"){
                val photoToDelete = bundle.getInt("DELETE_PHOTO_INDEX")
                viewModel.deletePhoto(photoToDelete)
            }
        }

        //Navigate to photoDetailFragment
        navController.navigate(AgendaItemDetailFragmentDirections
            .actionAgendaItemDetailFragmentToPhotoDetailFragment(
                viewModel.photos.value[index].toString(), index))
    }

    override fun deleteAttendee(attendee: Attendee) {
        super.deleteAttendee(attendee)
        viewModel.removeAttendee(attendee)
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
                saveAgendaItem()
                navController.popBackStack()
            }

            taskDoneCircle.setOnClickListener{
                viewModel.setIsDone(!viewModel.isDone.value)
            }

            agendaItemTitle.setOnClickListener {
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

            startTime.setOnClickListener {
                showTimePickerFragment(it)
            }
            startTimeButton.setOnClickListener {
                showTimePickerFragment(it)
            }
            endTime.setOnClickListener {
                showTimePickerFragment(it)
            }
            endTimeButton.setOnClickListener {
                showTimePickerFragment(it)
            }

            startDate.setOnClickListener {
                showDatePickerFragment(it)
            }
            startDateButton.setOnClickListener {
                showDatePickerFragment(it)
            }
            endDate.setOnClickListener {
                showDatePickerFragment(it)
            }
            endDateButton.setOnClickListener {
                showDatePickerFragment(it)
            }

            reminderTextView.setOnClickListener {
                showReminderOptionsPopupMenu(it)
            }

            reminderButton.setOnClickListener {
                showReminderOptionsPopupMenu(it)
            }

            addPhotoTextView.setOnClickListener{
                addPhoto()
            }
            addPhotoPlusSign.setOnClickListener{
                addPhoto()
            }

            deleteAgendaItemButton.setOnClickListener{
                if (isAttendee){
                    viewModel.switchAttendingStatus()
                }
                else{
                    showDeleteConfirmationDialogFragment()
                }
            }

            allButton.setOnClickListener {
                viewModel.showAllAttendees()
            }
            goingButton.setOnClickListener {
                viewModel.showGoingAttendees()
            }
            notGoingButton.setOnClickListener {
                viewModel.showNotGoingAttendees()
            }
        }
    }

    private fun addPhoto(){
        addPhotoSearchForResult.launch("image/*")
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
                val text = binding.agendaItemTitle.text.toString()
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

    private fun showTimePickerFragment(view: View){
        val timePickerFragment = TimePickerFragment()
        val supportFragmentManager = requireActivity().supportFragmentManager

        supportFragmentManager.setFragmentResultListener(
            "REQUEST_KEY",
            viewLifecycleOwner
        ){
            resultKey, bundle -> if(resultKey == "REQUEST_KEY"){
                val time = bundle.getString("SELECTED_TIME")
                val formatter = DateTimeFormatter.ofPattern("hh:mm a")
                if(view == binding.startTime || view == binding.startTimeButton){
                    viewModel.setStartTime(LocalTime.parse(time, formatter))
                }
                else if(view == binding.endTime || view == binding.endTimeButton){
                    viewModel.setEndTime(LocalTime.parse(time, formatter))
                }
            }
        }
        timePickerFragment.show(supportFragmentManager, "TimePickerFragment")
    }

    private fun showDatePickerFragment(view: View){
        val datePickerFragment = DatePickerFragment()
        val supportFragmentManager = requireActivity().supportFragmentManager

        supportFragmentManager.setFragmentResultListener(
            "REQUEST_KEY",
            viewLifecycleOwner
        ){
            resultKey, bundle -> if(resultKey == "REQUEST_KEY"){
                val date = bundle.getString("SELECTED_DATE")
                val formatter = DateTimeFormatter.ofPattern("MMM dd yyyy")

                if(view == binding.startDate || view == binding.startDateButton){
                    viewModel.setStartDate(LocalDate.parse(date, formatter))
                }
                else if(view == binding.endDate || view == binding.endDateButton){
                    viewModel.setEndDate(LocalDate.parse(date, formatter))
                }
            }
        }
        datePickerFragment.show(supportFragmentManager, "DatePickerFragment")
    }

    private fun setEditMode(isEditing: Boolean){
        binding.apply {
            //Changes label at top from current date to EDIT + agenda type
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

            if(!isAttendee){
                addAttendeeButton.isVisible = isEditing
            }

            saveButton.isVisible = isEditing
            saveButton.isEnabled = isEditing

            editButton.isVisible = !isEditing
            editButton.isEnabled = !isEditing

            editTitleButton.isVisible = isEditing
            editTitleButton.isEnabled = isEditing
            agendaItemTitle.isEnabled = isEditing

            editDescriptionButton.isVisible = isEditing
            editDescriptionButton.isEnabled = isEditing
            descriptionTextView.isEnabled = isEditing

            startTime.isEnabled = isEditing
            startDate.isEnabled = isEditing

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

    private fun getAgendaItemCurrentInfo(): AgendaItem {
        return AgendaItem(
            agendaItemType,
            viewModel.isDone.value,
            viewModel.title.value,
            viewModel.description.value,
            LocalDateTime.of(viewModel.selectedStartDate.value, viewModel.selectedStartTime.value),
            reminderTime = viewModel.selectedReminderTime.value,
            photos = viewModel.photos.value
        )
    }

    private fun saveAgendaItem(){
        val newAgendaItem = getAgendaItemCurrentInfo()
        if(args.agendaItem != null) {
            args.agendaItem?.let { oldAgendaItem ->
                AgendaItems.replaceAgendaItem(newAgendaItem,oldAgendaItem)
            }
        }
        else{
            AgendaItems.addAgendaItem(newAgendaItem)
        }
    }

    private fun showDeleteConfirmationDialogFragment(){
        val deleteConfirmationDialogFragment = DeleteConfirmationDialogFragment()
        val supportFragmentManager = requireActivity().supportFragmentManager

        //If deleteButton is pressed, deletes agenda item and pops backstack
        supportFragmentManager.setFragmentResultListener(
            "REQUEST_KEY",
            viewLifecycleOwner
        ){
            resultKey, bundle -> if(resultKey == "REQUEST_KEY"){
                val deleteAgendaItem = bundle.getBoolean("DELETE_AGENDA_ITEM")
                if (deleteAgendaItem){
                    args.agendaItem?.let {
                        AgendaItems.deleteAgendaItem(it)
                    }
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