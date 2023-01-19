package com.andrew.tasky.agenda.data.event

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.andrew.tasky.agenda.data.util.Converters

@Database(
    entities = [EventDto::class],
    version = 1
)

@TypeConverters(Converters::class)
abstract class EventDatabase : RoomDatabase() {

    abstract fun getEventDao(): EventDao
}
