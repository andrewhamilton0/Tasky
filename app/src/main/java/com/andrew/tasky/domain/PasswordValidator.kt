package com.andrew.tasky.domain

class PasswordValidator {

    fun validate(password: String): Boolean {
        val containsLowerCase = password.any { it.isLowerCase() }
        val containsUpperCase = password.any { it.isUpperCase() }
        val containsNumber = password.any { it.isDigit() }
        if(password.length < MIN_PASSWORD_LENGTH || !containsLowerCase  || !containsUpperCase || !containsNumber) {
            return false
        }

        return true
    }

    companion object {
        const val MIN_PASSWORD_LENGTH = 9
    }
}