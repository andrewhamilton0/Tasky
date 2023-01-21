package com.andrew.tasky.agenda.data.agenda

data class SyncAgendaRequest(
    val deletedEventIds: List<String>,
    val deletedTaskIds: List<String>,
    val deletedReminderIds: List<String>
)
