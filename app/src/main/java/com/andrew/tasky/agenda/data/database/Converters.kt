package com.andrew.tasky.agenda.data.database

import androidx.room.TypeConverter
import com.andrew.tasky.agenda.data.networkmodels.RemoteEventPhotoDto
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
    fun fromRemoteEventPhotoDtoList(list: List<RemoteEventPhotoDto>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toRemoteEventPhotoDtoList(value: String): List<RemoteEventPhotoDto> {
        return Gson().fromJson(value, Array<RemoteEventPhotoDto>::class.java).toList()
    }
}
