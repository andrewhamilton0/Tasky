package com.andrew.tasky.domain

class StringToInitials {

    fun convertStringToInitials(name: String):String {
        // Trims name for crash safety
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) return ""

        //Gets initials and capitalizes it
        val nameParts = trimmedName.split(' ')
        return if(nameParts.size > 1){
            nameParts[0][0].uppercaseChar().toString() +
                    nameParts[nameParts.lastIndex][0].uppercaseChar().toString()
        }
        else {
            if (nameParts[0].toCharArray().size >= 2){
                nameParts[0][0].uppercaseChar().toString() +
                        nameParts[0][1].uppercaseChar().toString()
            }
            else nameParts[0].uppercase()
        }
    }
}