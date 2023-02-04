package com.andrew.tasky.agenda.domain

import com.andrew.tasky.agenda.domain.models.AgendaItem

interface TaskRepository {
    suspend fun createTask(task: AgendaItem.Task)
    suspend fun updateTask(task: AgendaItem.Task)
    suspend fun deleteTask(task: AgendaItem.Task)
    suspend fun uploadCreateAndUpdateModifiedTasks()
}
