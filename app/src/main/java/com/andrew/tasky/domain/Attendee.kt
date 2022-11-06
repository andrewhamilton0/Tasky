package com.andrew.tasky.domain

import java.io.Serializable

data class Attendee(
    val name: String,
    val isAttending: Boolean,
    val attendeeType: AttendeeType,
    val email: String
) : Serializable

enum class AttendeeType {
    CREATOR,
    ATTENDEE
}
