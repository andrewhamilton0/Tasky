package com.andrew.tasky.agenda.presentation.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.andrew.tasky.R
import com.andrew.tasky.agenda.util.collectLatestLifecycleFlow
import com.andrew.tasky.databinding.DialogAddAttendeeBinding
import kotlinx.coroutines.flow.Flow

class AddAttendeeDialog(
    private val onSuccess: Flow<Unit>,
    private val onEmailResult: (String) -> Unit
) : DialogFragment(R.layout.dialog_add_attendee) {

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
                onEmailResult(result)
            }
            closeButton.setOnClickListener {
                dismiss()
            }
        }
        collectLatestLifecycleFlow(onSuccess) {
            dismiss()
        }
    }
}
