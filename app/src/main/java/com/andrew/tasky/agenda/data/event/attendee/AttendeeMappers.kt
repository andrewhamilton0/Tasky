package com.andrew.tasky.agenda.data.event.attendee

import com.andrew.tasky.agenda.data.networkmodels.AttendeeDto
import com.andrew.tasky.agenda.data.networkmodels.GetAttendeeResponseAttendeeDto
import com.andrew.tasky.agenda.domain.models.Attendee

fun AttendeeDto.toAttendee(hostId: String): Attendee {
    return Attendee(
        email = email,
        fullName = fullName,
        userId = userId,
        eventId = eventId,
        isGoing = isGoing,
        remindAt = remindAt,
        isCreator = hostId == userId
    )
}

fun GetAttendeeResponseAttendeeDto.toAttendee(): Attendee {
    return Attendee(
        email = email,
        fullName = fullName,
        userId = userId,
        eventId = null,
        isCreator = false, // Response returns if isCreator in api error message
        remindAt = null,
        isGoing = true
    )
}
