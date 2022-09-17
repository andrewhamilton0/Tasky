package com.andrew.tasky.presentation.views

import android.content.Context
import android.util.AttributeSet
import com.andrew.tasky.R
import com.andrew.tasky.databinding.PasswordTextFieldBinding
import com.google.android.material.textfield.TextInputLayout


class PasswordTextField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
): TextInputLayout(context, attrs) {

    private val binding = PasswordTextFieldBinding.bind(
        inflate(context, R.layout.password_text_field, this)
    )

    init {
    }
}