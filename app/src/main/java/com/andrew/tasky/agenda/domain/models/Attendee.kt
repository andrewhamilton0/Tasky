package com.andrew.tasky.agenda.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attendee(
    val email: String,
    val fullName: String,
    val userId: String,
    val eventId: String?,
    val isGoing: Boolean,
    val remindAt: Long?,
    val isCreator: Boolean
) : Parcelable
