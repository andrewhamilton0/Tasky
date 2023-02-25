package com.andrew.tasky.agenda.data.event.attendee

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
