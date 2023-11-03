package com.andrew.tasky.agenda.data.event

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andrew.tasky.agenda.data.networkmodels.RemoteEventPhotoDto
import com.andrew.tasky.agenda.domain.models.Attendee

@Entity
data class EventEntity(
    @PrimaryKey val id: String,
    val isDone: Boolean,
    val title: String,
    val description: String,
    val startDateAndTime: Long,
    val endDateAndTime: Long,
    val reminderTime: Long,
    val remotePhotos: List<RemoteEventPhotoDto>,
    val localPhotosKeys: List<String>,
    val isCreator: Boolean,
    val attendees: List<Attendee>,
    val host: String?,
    val remoteDeletedPhotos: List<RemoteEventPhotoDto> = emptyList(),
    val isGoing: Boolean
)
