package com.andrew.tasky.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.util.Patterns
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.andrew.tasky.R
import com.andrew.tasky.databinding.TextFieldEmailBinding
import java.util.regex.Pattern

class EmailTextField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
): ConstraintLayout(context, attrs) {

    private val binding = TextFieldEmailBinding.bind(
        inflate(context, R.layout.text_field_email, this)
    )

    init {
        binding.emailAddressEditText.addTextChangedListener {
            binding.emailAddressCheckBox.isVisible =
                it?.let { input -> Patterns.EMAIL_ADDRESS.matcher(input).matches() } == true
        }
    }
}