package com.andrew.tasky.agenda.data.networkModels

data class GetAttendeeResponse(
    val doesUserExist: Boolean,
    val attendee: GetAttendeeResponseAttendeeDto
)
