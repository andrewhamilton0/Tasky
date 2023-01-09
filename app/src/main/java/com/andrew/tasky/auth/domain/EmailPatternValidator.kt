package com.andrew.tasky.auth.domain

interface EmailPatternValidator {
    fun isValidEmailPattern(email: String): Boolean
}
