package com.andrew.tasky.agenda.data.event

import com.andrew.tasky.agenda.data.event.attendee.AttendeeDto
import com.andrew.tasky.agenda.data.event.photo.RemoteEventPhotoDto

data class EventDto(
    val id: String,
    val title: String,
    val description: String,
    val from: Long,
    val to: Long,
    val remindAt: Long,
    val host: String,
    val isUserEventCreator: Boolean,
    val attendees: List<AttendeeDto>,
    val photos: List<RemoteEventPhotoDto>
)
