package com.andrew.tasky.presentation.task_detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.andrew.tasky.R
import com.andrew.tasky.databinding.FragmentTaskDetailBinding
import com.andrew.tasky.util.TaskType
import com.andrew.tasky.util.EditType
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

class TaskDetailFragment : Fragment(R.layout.fragment_task_detail) {

    private val calendar = Calendar.getInstance()
    private lateinit var viewModel: TaskDetailViewModel
    private lateinit var navController: NavController
    private lateinit var binding: FragmentTaskDetailBinding
    private lateinit var taskType: TaskType

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTaskDetailBinding.bind(view)
        navController = Navigation.findNavController(view)
        viewModel = ViewModelProvider(this).get(TaskDetailViewModel::class.java)
        taskType = TaskType.values()[arguments?.getInt("taskType")!!]

        subscribeToObservables()
        setupTaskType()

        if(viewModel.getEditMode()){
            editMode()
        }
        else{
            nonEditMode()
        }

        binding.closeButton.setOnClickListener {
            navController.popBackStack()
        }

        binding.editButton.setOnClickListener {
            viewModel.setEditMode(true)
            editMode()
        }

        binding.saveButton.setOnClickListener {
            navController.popBackStack()
        }

        binding.taskTitle.setOnClickListener {
            val editType = EditType.TITLE.ordinal
            val bundle = bundleOf("editType" to editType)
            navController.navigate(R.id.action_taskDetailFragment_to_editFragment, bundle)
        }

        binding.editTitleButton.setOnClickListener {
            val editType = EditType.TITLE.ordinal
            val bundle = bundleOf("editType" to editType)
            navController.navigate(R.id.action_taskDetailFragment_to_editFragment, bundle)
        }

        binding.editDescriptionButton.setOnClickListener {
            val editType= EditType.DESCRIPTION.ordinal
            val bundle = bundleOf("editType" to editType)
            navController.navigate(R.id.action_taskDetailFragment_to_editFragment, bundle)
        }

        binding.descriptionTextView.setOnClickListener {
            val editType= EditType.DESCRIPTION.ordinal
            val bundle = bundleOf("editType" to editType)
            navController.navigate(R.id.action_taskDetailFragment_to_editFragment, bundle)
        }

        binding.time.setOnClickListener {
            timePickerFragment()
        }

        binding.date.setOnClickListener {
            datePickerFragment()
        }
    }

    private fun subscribeToObservables() {
        lifecycleScope.launchWhenStarted {
            viewModel.selectedDate.collectLatest {
                binding.date.text = it
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.selectedTime.collectLatest {
                binding.time.text = it
            }
        }
    }

    private fun timePickerFragment(){

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

    private fun datePickerFragment(){
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

    private fun editMode(){
        binding.currentDateTextView.text = getString(R.string.edit_task_text)

        binding.saveButton.isVisible = true
        binding.saveButton.isClickable = true

        binding.editButton.isVisible = false
        binding.editButton.isClickable = false

        binding.editTitleButton.isVisible = true
        binding.editTitleButton.isClickable = true
        binding.taskTitle.isClickable = true

        binding.editDescriptionButton.isVisible = true
        binding.editDescriptionButton.isClickable = true
        binding.descriptionTextView.isClickable = true

        binding.time.isClickable = true
        binding.date.isClickable = true

        binding.reminderButton.isClickable = true
        binding.reminderButton.isVisible = true
    }

    private fun nonEditMode(){
        binding.currentDateTextView.text = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).format((calendar.time)).uppercase()

        binding.saveButton.isVisible = false
        binding.saveButton.isClickable = false

        binding.editButton.isVisible = true
        binding.editButton.isClickable = true

        binding.editTitleButton.isVisible = false
        binding.editTitleButton.isClickable = false
        binding.taskTitle.isClickable = false

        binding.editDescriptionButton.isVisible = false
        binding.editDescriptionButton.isClickable = false
        binding.descriptionTextView.isClickable = false

        binding.time.isClickable = false
        binding.date.isClickable = false

        binding.reminderButton.isClickable = false
        binding.reminderButton.isVisible = false
    }

    private fun setupTaskType(){
        when (taskType) {
            TaskType.TASK -> {
                binding.taskColorBox.setImageResource(R.drawable.task_icon_box)
                binding.taskType.text = getString(R.string.task)
            }
            TaskType.EVENT -> {
                binding.taskColorBox.setImageResource(R.drawable.event_icon_box)
                binding.taskType.text = getString(R.string.event)
            }
            TaskType.REMINDER -> {
                binding.taskColorBox.setImageResource(R.drawable.reminder_icon_box)
                binding.taskType.text = getString(R.string.reminder)
            }
        }
    }
}