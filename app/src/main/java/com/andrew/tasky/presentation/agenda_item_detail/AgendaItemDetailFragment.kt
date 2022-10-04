package com.andrew.tasky.presentation.agenda_item_detail

import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.andrew.tasky.R
import com.andrew.tasky.databinding.DialogDeleteConfirmationBinding
import com.andrew.tasky.databinding.FragmentAgendaItemDetailBinding
import com.andrew.tasky.util.AgendaItemType
import com.andrew.tasky.util.EditType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
        viewModel = ViewModelProvider(this).get(AgendaItemDetailViewModel::class.java)
        agendaItemType = AgendaItemType.valueOf(args.agendaItemType)

        subscribeToObservables()
        setupAgendaItemType()
        onClickListeners()
    }

    fun <T> Fragment.collectLatestLifecycleFlow(flow: Flow<T>, onCollect: suspend (T) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collectLatest {
                    onCollect(it)
                }
            }
        }
    }

    private fun subscribeToObservables() {
        collectLatestLifecycleFlow(viewModel.selectedDate) { date ->
            binding.date.text = date
        }
        collectLatestLifecycleFlow(viewModel.selectedTime) { time ->
            binding.time.text = time
        }
        collectLatestLifecycleFlow(viewModel.title) { taskTitle ->
            binding.taskTitle.text = taskTitle
        }
        collectLatestLifecycleFlow(viewModel.description) { description ->
            binding.descriptionTextView.text = description
        }
        collectLatestLifecycleFlow(viewModel.isInEditMode) { editMode ->
            setEditMode(editMode)
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
                showDeleteDialog()
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

                navController.navigate(R.id.action_agendaItemDetailFragment_to_editFragment)
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
            saveButton.isClickable = isEditing

            editButton.isVisible = !isEditing
            editButton.isClickable = !isEditing

            editTitleButton.isVisible = isEditing
            editTitleButton.isClickable = isEditing
            taskTitle.isClickable = isEditing

            editDescriptionButton.isVisible = isEditing
            editDescriptionButton.isClickable = isEditing
            descriptionTextView.isClickable = isEditing

            time.isClickable = isEditing
            timeButton.isVisible = isEditing
            timeButton.isClickable=isEditing
            date.isClickable = isEditing
            dateButton.isVisible = isEditing
            dateButton.isClickable = isEditing

            reminderTextView.isClickable = isEditing
            reminderButton.isClickable = isEditing
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
                        text = getString(R.string.ten_minutes_before)
                        true
                    }
                    R.id.thirtyMinutes -> {
                        text = getString(R.string.thirty_minutes_before)
                        true
                    }
                    R.id.oneHour -> {
                        text = getString(R.string.one_hour_before)
                        true
                    }
                    R.id.sixHours -> {
                        text = getString(R.string.six_hours_before)
                        true
                    }
                    R.id.oneDay -> {
                        text = getString(R.string.one_day_before)
                        true
                    }
                    else -> true
                }
            }
        }
        popupMenu.show()
    }

    private fun showDeleteDialog(){
        val deleteDialog = Dialog(requireActivity())
        deleteDialog.setContentView(R.layout.dialog_delete_confirmation)
        val deleteDialogBinding = DialogDeleteConfirmationBinding.inflate(LayoutInflater.from(context))
        when(agendaItemType){
            AgendaItemType.TASK -> {
                deleteDialogBinding.confirmationTextView.text = getString(R.string.task_delete_confirmation)
            }
            AgendaItemType.EVENT -> {
                deleteDialogBinding.confirmationTextView.text = getString(R.string.event_delete_confirmation)
            }
            AgendaItemType.REMINDER -> {
                deleteDialogBinding.confirmationTextView.text = getString(R.string.reminder_delete_confirmation)
            }
        }
        deleteDialog.setContentView(deleteDialogBinding.root)
        deleteDialog.show()

        deleteDialogBinding.deleteButton.setOnClickListener{
            deleteDialog.cancel()
            navController.popBackStack()
        }
        deleteDialogBinding.cancelButton.setOnClickListener{
            deleteDialog.cancel()
        }
    }

}