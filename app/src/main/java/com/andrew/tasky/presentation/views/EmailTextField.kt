package com.andrew.tasky.presentation.views

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.andrew.tasky.R
import com.andrew.tasky.databinding.EmailTextFieldBinding

class EmailTextField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
): ConstraintLayout(context, attrs) {

    private val binding = EmailTextFieldBinding.bind(
        inflate(context, R.layout.email_text_field, this)
    )

    init {
        binding.emailAddressEditText.addTextChangedListener(){
            binding.emailAddressCheckBox.isVisible = binding.emailAddressEditText.text.toString() != ""
        }
    }
}