package com.andrew.tasky.agenda.data.reminder

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.andrew.tasky.agenda.data.util.Converters

@Database(
    entities = [ReminderEntity::class],
    version = 1
)

@TypeConverters(Converters::class)
abstract class ReminderDatabase : RoomDatabase() {

    abstract fun getReminderDao(): ReminderDao
}
