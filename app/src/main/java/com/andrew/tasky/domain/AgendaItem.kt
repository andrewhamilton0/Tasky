package com.andrew.tasky.domain

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andrew.tasky.util.AgendaItemType
import com.andrew.tasky.util.ReminderTime
import java.io.Serializable
import java.time.LocalDateTime

@Entity(
    tableName = "agendaItems"
)

data class AgendaItem(

    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "type")val type: AgendaItemType,
    @ColumnInfo(name = "is_done")var isDone: Boolean,
    @ColumnInfo(name = "title")val title: String,
    @ColumnInfo(name = "description")val description: String,
    @ColumnInfo(name = "start_date_and_time")val startDateAndTime: LocalDateTime,
    @ColumnInfo(name = "end_date_and_time")val endDateAndTime: LocalDateTime? = null,
    @ColumnInfo(name = "reminder_time")val reminderTime: ReminderTime,
    @ColumnInfo(name = "photos")val photos: List<Uri>? = null,
    @ColumnInfo(name = "is_attendee")val isAttendee: Boolean? = false,
    @ColumnInfo(name = "attendees")val attendees: List<Attendee>? = null,
    @ColumnInfo(name = "is_attending")val isAttending: Boolean? = true
) : Serializable
