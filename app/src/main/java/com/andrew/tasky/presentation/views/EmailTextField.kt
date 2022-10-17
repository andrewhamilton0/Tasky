package com.andrew.tasky.presentation.views

import android.content.Context
import android.util.AttributeSet
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

    private val emailRegex = "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`" +
            "{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]" +
            "|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9]" +
            ")?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?" +
            ")\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\" +
            "x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])"
    private val emailAddressPattern = Pattern.compile(emailRegex)

    init {
        binding.emailAddressEditText.addTextChangedListener {
            binding.emailAddressCheckBox.isVisible =
                it?.let { input -> emailAddressPattern.matcher(input).matches() } == true
        }
    }
}