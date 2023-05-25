package com.andrew.tasky.agenda.presentation.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.time.LocalDate

class DatePickerDialog(
    private val initialDate: LocalDate,
    private val onResult: (LocalDate) -> Unit
) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val year = initialDate.year
        // -1 fixes error where calendar shows a month ahead
        val month = initialDate.monthValue - 1
        val day = initialDate.dayOfMonth

        return DatePickerDialog(requireActivity(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        // +1 fixes error where returns a month before
        val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
        onResult(selectedDate)
    }
}
