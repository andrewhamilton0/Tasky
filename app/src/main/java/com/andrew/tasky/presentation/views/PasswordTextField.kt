package com.andrew.tasky.presentation.views

import android.content.Context
import android.util.AttributeSet
import com.andrew.tasky.R
import com.andrew.tasky.databinding.TextFieldPasswordBinding
import com.google.android.material.textfield.TextInputLayout


class PasswordTextField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
): TextInputLayout(context, attrs) {

    private val binding = TextFieldPasswordBinding.bind(
        inflate(context, R.layout.text_field_password, this)
    )

    init {
    }
}