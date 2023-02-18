package com.andrew.tasky.agenda.presentation.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.andrew.tasky.R
import com.andrew.tasky.databinding.DialogAddAttendeeBinding

class AddAttendeeDialog() : DialogFragment(R.layout.dialog_add_attendee) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = DialogAddAttendeeBinding.bind(view)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.apply {
            addVisitorButton.isEnabled = false
            emailTextField.binding.emailAddressEditText.addTextChangedListener { input ->
                addVisitorButton.isEnabled = emailTextField.isEmailValid(input.toString())
            }
            addVisitorButton.setOnClickListener {
                val result = emailTextField.getText()
                setFragmentResult("REQUEST_KEY", bundleOf("ATTENDEE" to result))
            }
            closeButton.setOnClickListener {
                dismiss()
            }
        }
    }
}
