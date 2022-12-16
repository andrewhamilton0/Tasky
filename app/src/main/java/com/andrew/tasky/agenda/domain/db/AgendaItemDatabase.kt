package com.andrew.tasky.agenda.domain.db

import androidx.room.*
import com.andrew.tasky.agenda.domain.models.AgendaItem

@Database(
    entities = [AgendaItem::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AgendaItemDatabase : RoomDatabase() {

    abstract fun getAgendaItemDao(): AgendaItemDao
}
