package com.andrew.tasky.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.widget.Toast
import com.andrew.tasky.R
import com.andrew.tasky.databinding.TextFieldPasswordBinding
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Pattern


class PasswordTextField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
): TextInputLayout(context, attrs) {

    private val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{9,}\$"

    private val passwordPattern = Pattern.compile(
        passwordRegex
    )

    private val binding = TextFieldPasswordBinding.bind(
        inflate(context, R.layout.text_field_password, this)
    )

    fun isPasswordValid(): Boolean{
        return if(binding.passwordEditText.text?.let { passwordPattern.matcher(it).matches() } == true){
            true
        } else{
            Toast.makeText(context, "Password must contain at least 9 characters, one number, one uppercase, and one lowercase letter", Toast.LENGTH_LONG).show()
            false
        }
    }
}