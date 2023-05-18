package com.andrew.tasky.agenda.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.andrew.tasky.agenda.data.event.EventDao
import com.andrew.tasky.agenda.data.event.EventEntity
import com.andrew.tasky.agenda.data.event.ModifiedEventEntity
import com.andrew.tasky.agenda.data.reminder.ModifiedReminderEntity
import com.andrew.tasky.agenda.data.reminder.ReminderDao
import com.andrew.tasky.agenda.data.reminder.ReminderEntity
import com.andrew.tasky.agenda.data.task.ModifiedTaskEntity
import com.andrew.tasky.agenda.data.task.TaskDao
import com.andrew.tasky.agenda.data.task.TaskEntity

@Database(
    entities = [
        ReminderEntity::class,
        ModifiedReminderEntity::class,
        TaskEntity::class,
        ModifiedTaskEntity::class,
        EventEntity::class,
        ModifiedEventEntity::class
    ],
    version = 1
)

@TypeConverters(Converters::class)
abstract class AgendaDatabase : RoomDatabase() {

    abstract fun getReminderDao(): ReminderDao
    abstract fun getTaskDao(): TaskDao
    abstract fun getEventDao(): EventDao
}
