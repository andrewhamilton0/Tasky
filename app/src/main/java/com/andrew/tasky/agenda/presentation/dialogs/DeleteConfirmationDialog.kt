package com.andrew.tasky.agenda.presentation.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.andrew.tasky.R
import com.andrew.tasky.databinding.DialogDeleteConfirmationBinding

class DeleteConfirmationDialog() : DialogFragment(R.layout.dialog_delete_confirmation) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val deleteDialogBinding = DialogDeleteConfirmationBinding.bind(view)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setFragmentResultListener("DELETE_CONFIRMATION_REQUEST_KEY") {
            resultKey, bundle ->
            if (resultKey == "DELETE_CONFIRMATION_REQUEST_KEY") {
                val deletionTypeName = bundle.getString("DELETE_ITEM_NAME")
                deleteDialogBinding.confirmationTextView.text = getString(
                    R.string.are_you_sure_you_want_to_delete_this_string, deletionTypeName
                )
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
