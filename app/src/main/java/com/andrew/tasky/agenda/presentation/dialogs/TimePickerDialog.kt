package com.andrew.tasky.agenda.presentation.dialogs

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.time.LocalTime

class TimePickerDialog(
    private val onResult: (LocalTime) -> Unit,
    private val initialTime: LocalTime
) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val hour = initialTime.hour
        val minute = initialTime.minute

        return TimePickerDialog(requireActivity(), this, hour, minute, false)
    }

    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
        val selectedTime = LocalTime.of(hour, minute)
        onResult(selectedTime)
    }
}
