package com.andrew.tasky.agenda.presentation.dialogs

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class TimePickerDialog : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val hour = 8
        val minute = 0

        return TimePickerDialog(requireActivity(), this, hour, minute, false)
    }

    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {

        val selectedTime = LocalTime.of(hour, minute)
            .format(DateTimeFormatter.ofPattern("hh:mm a"))

        val selectedTimeBundle = Bundle()
        selectedTimeBundle.putString("SELECTED_TIME", selectedTime)

        setFragmentResult("REQUEST_KEY", selectedTimeBundle)
    }
}
