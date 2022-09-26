package com.andrew.tasky.presentation.task_detail

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.text.SimpleDateFormat
import java.util.*

class TimePickerFragment: DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private val calendar = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val hour = 8
        val minute = 0

        return TimePickerDialog(requireActivity(), this, hour, minute, false)
    }

    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)

        val selectedTime = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format((calendar.time))

        val selectedDateBundle = Bundle()
        selectedDateBundle.putString("SELECTED_TIME", selectedTime)

        setFragmentResult("REQUEST_KEY", selectedDateBundle)
    }
}