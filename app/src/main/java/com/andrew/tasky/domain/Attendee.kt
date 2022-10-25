package com.andrew.tasky.domain

import android.provider.ContactsContract.CommonDataKinds.Email
import java.io.Serializable

data class Attendee(
    val name: String,
    val isAttending: Boolean,
    val attendeeType: AttendeeType,
    val email: Email? = null
) : Serializable


enum class AttendeeType {
    CREATOR,
    ATTENDEE
}