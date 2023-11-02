package com.andrew.tasky.agenda.data.networkModels

data class GetAttendeeResponseAttendeeDto(
    val email: String,
    val fullName: String,
    val userId: String
)
