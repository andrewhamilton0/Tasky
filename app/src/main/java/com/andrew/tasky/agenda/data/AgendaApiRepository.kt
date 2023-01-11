package com.andrew.tasky.agenda.data

import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.models.Attendee
import com.andrew.tasky.auth.data.AuthResult

interface AgendaApiRepository {
    suspend fun getAgendaItems(timezone: String, time: Long): AuthResult<List<AgendaItem>>
    suspend fun syncAgendaItems(): AuthResult<SyncAgendaResponse>
    suspend fun createEvent(): AuthResult<Unit> // TODO
    suspend fun getEvent(eventId: String): AuthResult<AgendaItem>
    suspend fun deleteEvent(eventId: String): AuthResult<Unit>
    suspend fun updateEvent(): AuthResult<Unit> // TODO
    suspend fun getAttendee(email: String): AuthResult<Attendee>
    suspend fun deleteAttendee(eventId: String): AuthResult<Unit>
    suspend fun createTask(task: AgendaItem): AuthResult<Unit>
    suspend fun updateTask(task: AgendaItem): AuthResult<Unit>
    suspend fun getTask(taskId: String): AuthResult<AgendaItem>
    suspend fun deleteTask(taskId: String): AuthResult<Unit>
    suspend fun createReminder(reminderResponse: Reminder): AuthResult<Unit>
    suspend fun updateReminder(reminderResponse: Reminder): AuthResult<Unit>
    suspend fun getReminder(reminderId: String): AuthResult<AgendaItem>
    suspend fun deleteReminder(reminderId: String): AuthResult<Unit>
}
