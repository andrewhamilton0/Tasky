package com.andrew.tasky.presentation.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.*
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.andrew.tasky.R
import com.andrew.tasky.presentation.register.RegisterActivity


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val emailAddress = findViewById<EditText>(R.id.emailAddressEditText)
        val emailAddressCheckBox = findViewById<ImageView>(R.id.emailAddressCheckBox)
        val password = findViewById<EditText>(R.id.passwordEditText)
        val passwordVisibilityButton = findViewById<ImageButton>(R.id.passwordVisibilityButton)
        var passwordCursorPlace = Int
        var passwordVisibility = false
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpButton = findViewById<TextView>(R.id.signUpButton)






        signUpButton.setOnClickListener(){
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }




        emailAddress.addTextChangedListener(){
            emailAddressCheckBox.isVisible = emailAddress.text.toString() != ""
        }

        password.addTextChangedListener(){

        }


        passwordVisibilityButton.setOnClickListener(){
            if(!passwordVisibility){
                passwordVisibilityButton.setBackgroundResource(R.drawable.ic_baseline_visibility_24)
                passwordVisibility = true
                password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                }
            else{
                passwordVisibilityButton.setBackgroundResource(R.drawable.ic_baseline_visibility_off_24)
                passwordVisibility = false
                password.transformationMethod = PasswordTransformationMethod.getInstance()
            }

        }

    }
}



