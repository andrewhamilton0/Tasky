package com.andrew.tasky.agenda.data.event

import com.andrew.tasky.agenda.data.util.ReminderTimeConversion
import com.andrew.tasky.agenda.data.util.localDateTimeToZonedEpochMilli
import com.andrew.tasky.agenda.domain.models.AgendaItem

fun AgendaItem.Event.toCreateEventRequest(): CreateEventRequest {
    return CreateEventRequest(
        id = id,
        title = title,
        description = description,
        from = localDateTimeToZonedEpochMilli(startDateAndTime),
        to = localDateTimeToZonedEpochMilli(endDateAndTime),
        remindAt = ReminderTimeConversion.toEpochMilli(
            reminderTime = reminderTime,
            startLocalDateTime = startDateAndTime
        ),
        attendeeIds = attendees?.map {
            it.userId
        } ?: emptyList()
    )
}
