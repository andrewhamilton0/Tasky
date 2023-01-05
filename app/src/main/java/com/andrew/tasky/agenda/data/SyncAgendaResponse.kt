package com.andrew.tasky.agenda.data

data class SyncAgendaResponse(
    val deletedEventIds: List<String>,
    val deletedTaskIds: List<String>,
    val deletedReminderIds: List<String>
)
