package com.andrew.tasky.agenda.util

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation.findNavController
import com.andrew.tasky.R
import com.andrew.tasky.agenda.presentation.dialogs.AddAttendeeDialog
import com.andrew.tasky.agenda.presentation.dialogs.DatePickerDialog
import com.andrew.tasky.agenda.presentation.dialogs.DeleteConfirmationDialog
import com.andrew.tasky.agenda.presentation.dialogs.TimePickerDialog
import com.andrew.tasky.agenda.presentation.screens.edit.EditFragmentDirections
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

fun <T> Fragment.collectLatestLifecycleFlow(
    flow: Flow<T>,
    onCollect: suspend (T) -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest {
                onCollect(it)
            }
        }
    }
}

fun <T> AppCompatActivity.collectLatestLifecycleFlow(
    flow: Flow<T>,
    onCollect: suspend (T) -> Unit
) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest {
                onCollect(it)
            }
        }
    }
}

fun Fragment.navigateToEditFragment(
    editType: EditType,
    originalText: String,
    onResult: (String) -> Unit
) {
    val navController = findNavController(requireView())
    setFragmentResultListener("INPUT_REQUEST_KEY") { resultKey, bundle ->
        if (resultKey == "INPUT_REQUEST_KEY") {
            val input = bundle.getString("INPUT")
            when (editType) {
                EditType.TITLE -> {
                    if (input != null) {
                        onResult(input)
                    }
                }
                EditType.DESCRIPTION -> {
                    if (input != null) {
                        onResult(input)
                    }
                }
            }
        }
    }
    when (editType) {
        EditType.TITLE -> {
            val bundle = Bundle()
            bundle.putString("TEXT", originalText)
            bundle.putString("EDIT_TYPE", editType.name)

            setFragmentResult("EDIT_TYPE_AND_TEXT_REQUEST_KEY", bundle)

            navController.navigate(
                EditFragmentDirections.actionGlobalEditFragment()
            )
        }
        EditType.DESCRIPTION -> {
            val bundle = Bundle()
            bundle.putString("TEXT", originalText)
            bundle.putString("EDIT_TYPE", editType.name)

            setFragmentResult("EDIT_TYPE_AND_TEXT_REQUEST_KEY", bundle)

            navController.navigate(
                EditFragmentDirections
                    .actionGlobalEditFragment()
            )
        }
    }
}

fun Fragment.showDeleteConfirmationDialog(
    deleteItemName: String,
    onResultDeleteAgendaItem: () -> Unit
) {
    val deleteConfirmationDialog = DeleteConfirmationDialog()
    val supportFragmentManager = requireActivity().supportFragmentManager

    supportFragmentManager.setFragmentResultListener(
        "REQUEST_KEY",
        viewLifecycleOwner
    ) { resultKey, bundle ->
        if (resultKey == "REQUEST_KEY") {
            val deleteAgendaItem = bundle.getBoolean("DELETE_AGENDA_ITEM")
            if (deleteAgendaItem) {
                onResultDeleteAgendaItem()
            }
        }
    }

    val bundle = Bundle()
    bundle.putString("DELETE_ITEM_NAME", deleteItemName)
    supportFragmentManager.setFragmentResult(
        "DELETE_CONFIRMATION_REQUEST_KEY", bundle
    )

    deleteConfirmationDialog.show(supportFragmentManager, "DeleteConfirmationDialog")
}

fun Fragment.showReminderOptionsPopupMenu(view: View, onResult: (ReminderTime) -> Unit) {
    val popupMenu = PopupMenu(requireContext(), view)
    popupMenu.inflate(R.menu.menu_reminder_time_options)
    popupMenu.setOnMenuItemClickListener {
        when (it.itemId) {
            R.id.tenMinutes -> {
                onResult(ReminderTime.TEN_MINUTES_BEFORE)
                true
            }
            R.id.thirtyMinutes -> {
                onResult(ReminderTime.THIRTY_MINUTES_BEFORE)
                true
            }
            R.id.oneHour -> {
                onResult(ReminderTime.ONE_HOUR_BEFORE)
                true
            }
            R.id.sixHours -> {
                onResult(ReminderTime.SIX_HOURS_BEFORE)
                true
            }
            R.id.oneDay -> {
                onResult(ReminderTime.ONE_DAY_BEFORE)
                true
            }
            else -> true
        }
    }
    popupMenu.show()
}

fun Fragment.showDatePickerDialog(
    onResult: (LocalDate) -> Unit,
    initialDate: LocalDate
) {
    val datePickerFragment = DatePickerDialog(
        initialDate = initialDate,
        onResult = onResult
    )
    val supportFragmentManager = requireActivity().supportFragmentManager
    datePickerFragment.show(supportFragmentManager, "DatePickerFragment")
}

fun Fragment.showTimePickerDialog(
    onResult: (LocalTime) -> Unit,
    initialTime: LocalTime
) {
    val timePickerDialog = TimePickerDialog(
        onResult = onResult,
        initialTime = initialTime
    )
    val supportFragmentManager = requireActivity().supportFragmentManager
    timePickerDialog.show(supportFragmentManager, "TimePickerDialog")
}

fun Fragment.showAttendeeDialog(onEmailResult: (String) -> Unit, onSuccessListener: Flow<Unit>) {
    val attendeeDialog = AddAttendeeDialog(
        onSuccess = onSuccessListener,
        onEmailResult = onEmailResult
    )
    val supportFragmentManager = requireActivity().supportFragmentManager

    attendeeDialog.show(supportFragmentManager, "AddAttendeeDialog")
}
