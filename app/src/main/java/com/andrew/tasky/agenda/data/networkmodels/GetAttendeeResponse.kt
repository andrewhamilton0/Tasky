package com.andrew.tasky.agenda.data.networkmodels

data class GetAttendeeResponse(
    val doesUserExist: Boolean,
    val attendee: GetAttendeeResponseAttendeeDto
)
