package com.andrew.tasky.presentation.agenda_item_detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
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
import com.andrew.tasky.databinding.FragmentAgendaItemDetailBinding
import com.andrew.tasky.util.AgendaItemType
import com.andrew.tasky.util.EditType
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AgendaItemDetailFragment : Fragment(R.layout.fragment_agenda_item_detail) {

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

    private fun subscribeToObservables() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedDate.collect {
                    binding.date.text = it
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedTime.collectLatest {
                    binding.time.text = it
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.title.collectLatest {
                    binding.taskTitle.text = it
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.description.collectLatest {
                    binding.descriptionTextView.text = it
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isInEditMode.collectLatest {
                    setEditMode(it)
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
                editFragment(EditType.TITLE)
            }

            editTitleButton.setOnClickListener {
                editFragment(EditType.TITLE)
            }

            editDescriptionButton.setOnClickListener {
                editFragment(EditType.DESCRIPTION)
            }

            descriptionTextView.setOnClickListener {
                editFragment(EditType.DESCRIPTION)
            }

            time.setOnClickListener {
                showTimePickerFragment()
            }

            date.setOnClickListener {
                showDatePickerFragment()
            }
        }
    }

    private fun editFragment(editType: EditType){
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
            date.isClickable = isEditing

            reminderButton.isClickable = isEditing
            reminderButton.isVisible = isEditing
        }
    }
}