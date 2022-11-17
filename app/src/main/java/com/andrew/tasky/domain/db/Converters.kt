package com.andrew.tasky.domain.db

import androidx.room.TypeConverter
import com.andrew.tasky.domain.Attendee
import com.andrew.tasky.domain.models.Photo
import com.andrew.tasky.util.AgendaItemType
import com.andrew.tasky.util.ReminderTime
import com.google.gson.Gson
import java.time.LocalDateTime

class Converters {

    @TypeConverter
    fun fromAgendaItemType(agendaItemType: AgendaItemType): String {
        return agendaItemType.name
    }

    @TypeConverter
    fun toAgendaItemType(name: String): AgendaItemType {
        return AgendaItemType.valueOf(name)
    }

    @TypeConverter
    fun fromLocalDateTime(localDateTime: LocalDateTime?): String? {
        return localDateTime?.toString()
    }

    @TypeConverter
    fun toLocalDateTime(localDateTimeString: String?): LocalDateTime? {
        return if (localDateTimeString != null) {
            LocalDateTime.parse(localDateTimeString)
        } else null
    }

    @TypeConverter
    fun fromReminderTime(reminderTime: ReminderTime): String {
        return reminderTime.name
    }

    @TypeConverter
    fun toReminderTime(name: String): ReminderTime {
        return ReminderTime.valueOf(name)
    }

    @TypeConverter
    fun fromAttendeeList(list: List<Attendee>?): String? {
        return if (list != null) {
            Gson().toJson(list)
        } else null
    }

    @TypeConverter
    fun toAttendeeList(value: String?): List<Attendee>? {
        return if (value != null) {
            Gson().fromJson(value, Array<Attendee>::class.java).toList()
        } else null
    }

    @TypeConverter
    fun fromUriList(list: List<Photo>?): String? {
        return if (list != null) {
            Gson().toJson(list)
        } else null
    }

    @TypeConverter
    fun toUriList(value: String?): List<Photo>? {
        return if (value != null) {
            Gson().fromJson(value, Array<Photo>::class.java).toList()
        } else null
    }
}
