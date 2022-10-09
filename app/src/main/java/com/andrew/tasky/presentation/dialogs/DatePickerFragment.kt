package com.andrew.tasky.presentation.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DatePickerFragment: DialogFragment(), DatePickerDialog.OnDateSetListener {

    private val currentDateTime = LocalDateTime.now()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val year = currentDateTime.year
        //-1 to show the correct month, otherwise shows a month ahead
        val month = currentDateTime.monthValue-1
        val day = currentDateTime.dayOfMonth

        return DatePickerDialog(requireActivity(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        //+1 to return the correct month, otherwise returns one month before
        val selectedDate = LocalDate.of(year, month+1, dayOfMonth)
            .format(DateTimeFormatter.ofPattern("MMM dd yyyy"))

        val selectedDateBundle = Bundle()
        selectedDateBundle.putString("SELECTED_DATE", selectedDate)

        setFragmentResult("REQUEST_KEY", selectedDateBundle)
    }
}