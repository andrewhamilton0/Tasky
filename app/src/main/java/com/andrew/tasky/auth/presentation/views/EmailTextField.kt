package com.andrew.tasky.auth.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.util.Patterns
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.andrew.tasky.R
import com.andrew.tasky.databinding.CvTextFieldEmailBinding

class EmailTextField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding = CvTextFieldEmailBinding.bind(
        inflate(context, R.layout.cv_text_field_email, this)
    )

    init {
        binding.emailAddressEditText.addTextChangedListener {
            binding.emailAddressCheckBox.isVisible =
                it?.let { input -> Patterns.EMAIL_ADDRESS.matcher(input).matches() } == true
        }
    }
}
