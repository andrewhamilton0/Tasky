package com.andrew.tasky.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.widget.Toast
import com.andrew.tasky.R
import com.andrew.tasky.databinding.CvTextFieldPasswordBinding
import com.andrew.tasky.domain.PasswordValidator
import com.google.android.material.textfield.TextInputLayout

class PasswordTextField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : TextInputLayout(context, attrs) {

    private val binding = CvTextFieldPasswordBinding.bind(
        inflate(context, R.layout.cv_text_field_password, this)
    )

    fun isPasswordValid(): Boolean {
        return if (PasswordValidator().validate(binding.passwordEditText.text.toString())) {
            true
        } else {
            Toast.makeText(
                context,
                String.format(
                    resources.getString(R.string.error_invalid_password_format),
                    PasswordValidator.MIN_PASSWORD_LENGTH
                ),
                Toast.LENGTH_LONG
            ).show()
            false
        }
    }
}
