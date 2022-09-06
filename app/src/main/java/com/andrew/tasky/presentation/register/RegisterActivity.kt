package com.andrew.tasky.presentation.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.*
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.andrew.tasky.R
import com.andrew.tasky.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener(){
            finish()
        }

        binding.emailAddressEditText.addTextChangedListener(){
            binding.emailAddressCheckBox.isVisible = binding.emailAddressEditText.text.toString() != ""
        }

        // Much cleaner now since you don't need to define the logic for the PW field in every
        // screen you use it
        binding.passwordTextField.addOnTextChangeListener {

        }
    }
}