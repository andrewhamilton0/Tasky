package com.andrew.tasky.agenda.presentation.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.andrew.tasky.R
import com.andrew.tasky.agenda.util.AgendaItemType
import com.andrew.tasky.databinding.DialogDeleteConfirmationBinding

class DeleteConfirmationDialog() : DialogFragment(R.layout.dialog_delete_confirmation) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val deleteDialogBinding = DialogDeleteConfirmationBinding.bind(view)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setFragmentResultListener("DELETE_CONFIRMATION_AGENDA_TYPE_REQUEST_KEY") {
            resultKey, bundle ->
            if (resultKey == "DELETE_CONFIRMATION_AGENDA_TYPE_REQUEST_KEY") {
                val agendaItemType = bundle.getString("AGENDA_ITEM_TYPE")
                    ?.let { AgendaItemType.valueOf(it) }
                when (agendaItemType) {
                    AgendaItemType.TASK -> {
                        deleteDialogBinding.confirmationTextView.text = getString(
                            R.string.task_delete_confirmation
                        )
                    }
                    AgendaItemType.EVENT -> {
                        deleteDialogBinding.confirmationTextView.text = getString(
                            R.string.event_delete_confirmation
                        )
                    }
                    AgendaItemType.REMINDER -> {
                        deleteDialogBinding.confirmationTextView.text =
                            getString(R.string.reminder_delete_confirmation)
                    }
                    else -> {
                    }
                }
            }
        }

        deleteDialogBinding.deleteButton.setOnClickListener {
            dismiss()
            val bundle = Bundle()
            bundle.putBoolean("DELETE_AGENDA_ITEM", true)
            setFragmentResult("REQUEST_KEY", bundle)
        }

        deleteDialogBinding.cancelButton.setOnClickListener {
            dismiss()
        }
    }
}
