package com.andrew.tasky.domain

class NameValidator {

    fun validate(name: String): Boolean {
        return(name.trim().length in MIN_NAME_LENGTH..MAX_NAME_LENGTH)
    }

    companion object {
        const val MIN_NAME_LENGTH = 2
        const val MAX_NAME_LENGTH = 50
    }
}
