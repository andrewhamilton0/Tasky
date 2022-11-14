package com.andrew.tasky.domain.db

import android.net.Uri
import androidx.room.TypeConverter
import com.andrew.tasky.domain.Attendee
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
    fun fromLocalDateTime(localDateTime: LocalDateTime): String {
        return localDateTime.toString()
    }

    @TypeConverter
    fun toLocalDateTime(localDateTimeString: String): LocalDateTime {
        return LocalDateTime.parse(localDateTimeString)
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
    fun fromAttendeeList(list: List<Attendee>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toAttendeeList(value: String): List<Attendee> {
        return Gson().fromJson(value, Array<Attendee>::class.java).toList()
    }

    @TypeConverter
    fun fromUriList(list: List<Uri>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toUriList(value: String): List<Uri> {
        return Gson().fromJson(value, Array<Uri>::class.java).toList()
    }
}
