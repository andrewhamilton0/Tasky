package com.andrew.tasky.agenda.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.andrew.tasky.agenda.data.reminder.ModifiedReminderEntity
import com.andrew.tasky.agenda.data.reminder.ReminderDao
import com.andrew.tasky.agenda.data.reminder.ReminderEntity
import com.andrew.tasky.agenda.data.util.Converters

@Database(
    entities = [ReminderEntity::class, ModifiedReminderEntity::class],
    version = 1
)

@TypeConverters(Converters::class)
abstract class AgendaDatabase : RoomDatabase() {

    abstract fun getReminderDao(): ReminderDao
}
