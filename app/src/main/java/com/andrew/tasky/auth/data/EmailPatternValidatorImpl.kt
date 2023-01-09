package com.andrew.tasky.auth.data

import android.util.Patterns
import com.andrew.tasky.auth.domain.EmailPatternValidator

class EmailPatternValidatorImpl : EmailPatternValidator {

    override fun isValidEmailPattern(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
