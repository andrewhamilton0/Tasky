package com.andrew.tasky.agenda.data.event

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andrew.tasky.agenda.domain.models.Attendee
import com.andrew.tasky.agenda.domain.models.EventPhoto

@Entity
data class EventEntity(
    @PrimaryKey val id: String,
    val isDone: Boolean,
    val title: String,
    val description: String,
    val startDateAndTime: Long,
    val endDateAndTime: Long,
    val reminderTime: Long,
    val photos: List<EventPhoto> = emptyList(),
    val isCreator: Boolean,
    val attendees: List<Attendee> = emptyList(),
    val host: String,
    val deletedPhotoKeys: List<String> = emptyList(),
    val isGoing: Boolean
)
