package com.andrew.tasky.agenda.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
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
        ModifiedTaskEntity::class
    ],
    version = 1
)

abstract class AgendaDatabase : RoomDatabase() {

    abstract fun getReminderDao(): ReminderDao
    abstract fun getTaskDao(): TaskDao
}
