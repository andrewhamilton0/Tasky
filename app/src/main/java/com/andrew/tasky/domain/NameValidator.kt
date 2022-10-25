package com.andrew.tasky.domain

class NameValidator {

    fun validate(name: String): Boolean {
        //Verifies name is valid
        if(name.trim().length < MIN_NAME_LENGTH || name.trim().length > MAX_NAME_LENGTH ) {
            return false
        }

        return true
    }

    companion object {
        const val MIN_NAME_LENGTH = 2
        const val MAX_NAME_LENGTH = 50
    }
}