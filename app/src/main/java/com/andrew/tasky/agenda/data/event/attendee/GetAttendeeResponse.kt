package com.andrew.tasky.agenda.data.event.attendee

import com.andrew.tasky.agenda.domain.models.Attendee

data class GetAttendeeResponse(
    val doesUserExist: Boolean,
    val attendee: Attendee
)
