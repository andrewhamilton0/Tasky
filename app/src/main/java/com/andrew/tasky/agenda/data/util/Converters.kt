package com.andrew.tasky.agenda.data.util

import androidx.room.TypeConverter
import com.andrew.tasky.agenda.data.event.photo.LocalEventPhotoDto
import com.andrew.tasky.agenda.data.event.photo.RemoteEventPhotoDto
import com.andrew.tasky.agenda.domain.models.Attendee
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
    fun fromLocalEventPhotoDtoList(list: List<LocalEventPhotoDto>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toLocalEventPhotoDtoList(value: String): List<LocalEventPhotoDto> {
        return Gson().fromJson(value, Array<LocalEventPhotoDto>::class.java).toList()
    }

    @TypeConverter
    fun fromRemoteEventPhotoDtoList(list: List<RemoteEventPhotoDto>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toRemoteEventPhotoDtoList(value: String): List<RemoteEventPhotoDto> {
        return Gson().fromJson(value, Array<RemoteEventPhotoDto>::class.java).toList()
    }
}
