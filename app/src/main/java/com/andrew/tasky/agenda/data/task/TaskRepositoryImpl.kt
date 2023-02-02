package com.andrew.tasky.agenda.data.task

import com.andrew.tasky.agenda.domain.TaskRepository
import com.andrew.tasky.agenda.domain.models.AgendaItem

class TaskRepositoryImpl() : TaskRepository {
    override suspend fun createTask(task: AgendaItem.Task) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTask(task: AgendaItem.Task) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTask(task: AgendaItem.Task) {
        TODO("Not yet implemented")
    }

    override suspend fun uploadCreateAndUpdateModifiedReminders() {
        TODO("Not yet implemented")
    }
}
