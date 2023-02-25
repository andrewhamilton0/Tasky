package com.andrew.tasky.agenda.domain.models

data class Attendee(
    val email: String,
    val fullName: String,
    val userId: String,
    val eventId: String?,
    val isGoing: Boolean,
    val remindAt: Long,
    val isCreator: Boolean
)
