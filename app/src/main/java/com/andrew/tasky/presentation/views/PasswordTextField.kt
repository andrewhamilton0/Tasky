package com.andrew.tasky.presentation.views

import android.content.Context
import android.text.Editable
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import com.andrew.tasky.R
import com.andrew.tasky.databinding.PasswordTextFieldBinding

// As you see, you just define all the behavior that makes up a view in a custom class.
// That allows you to easily use this view just like a normal view wherever you like while
// having the logic at one single place. So if that changes, you only need to change this class.

// Ideally, this should extend EditText instead of ConstraintLayout because then it will also
// inherit all the default behavior and functions of an EditText view. But in that case, you'd need
// to implement this view differently and probably draw the visibility button using a canvas which gets
// more complex (that's why I just inherit from constraint layout here - the root of the XML layout
// of password_text_field.xml). Then we can just inflate it and treat it as we normally would.

// Also, if you use TextInputLayout + TextInputEdit text from the material library, that already
// offers a property to add a password toggle. I'm not sure though if that allows enough visual customization
// to make it look like in the mockups (I personally used Compose for this project), therefore this one here is fine.
class PasswordTextField @JvmOverloads constructor(
    context: Context,
    // The attribute set could be used to also pass custom arguments in XML, for example if you
    // want the PW to be initially visible or not - not needed here.
    attrs: AttributeSet? = null
): ConstraintLayout(context, attrs) {

    private val binding = PasswordTextFieldBinding.bind(
        inflate(context, R.layout.password_text_field, this)
    )

    private var isPasswordVisible = false

    init {
        binding.passwordVisibilityButton.setOnClickListener {
            if(!isPasswordVisible){
                binding.passwordVisibilityButton.setBackgroundResource(R.drawable.ic_baseline_visibility_24)
                isPasswordVisible = true
                binding.passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
            else{
                binding.passwordVisibilityButton.setBackgroundResource(R.drawable.ic_baseline_visibility_off_24)
                isPasswordVisible = false
                binding.passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
    }

    fun addOnTextChangeListener(onChange: (Editable?) -> Unit) {
        binding.passwordEditText.addTextChangedListener { onChange(it) }
    }
}