package com.andrew.tasky.agenda.domain.models

import java.io.Serializable

data class Attendee(
    val email: String,
    val fullName: String,
    val userId: String,
    val eventId: String,
    val isGoing: Boolean,
    val remindAt: Long,
    val attendeeType: AttendeeType
) : Serializable

enum class AttendeeType {
    CREATOR,
    ATTENDEE
}
