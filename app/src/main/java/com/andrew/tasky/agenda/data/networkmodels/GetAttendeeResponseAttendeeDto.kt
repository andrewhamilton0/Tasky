package com.andrew.tasky.agenda.data.networkmodels

data class GetAttendeeResponseAttendeeDto(
    val email: String,
    val fullName: String,
    val userId: String
)
