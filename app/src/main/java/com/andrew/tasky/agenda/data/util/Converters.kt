package com.andrew.tasky.agenda.data.util

import androidx.room.TypeConverter
import com.andrew.tasky.agenda.domain.models.Attendee
import com.andrew.tasky.agenda.domain.models.EventPhoto
import com.google.gson.Gson

class Converters {

    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return Gson().fromJson(value, Array<String>::class.java).toList()
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
    fun fromEventPhotoList(list: List<EventPhoto>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toEventPhotoList(value: String): List<EventPhoto> {
        return Gson().fromJson(value, Array<EventPhoto>::class.java).toList()
    }
}
