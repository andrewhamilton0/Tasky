package com.andrew.tasky.agenda.data.task

import com.andrew.tasky.agenda.data.networkmodels.TaskDto
import retrofit2.Response
import retrofit2.http.*

interface TaskApi {
    @POST("/task")
    suspend fun createTask(
        @Body taskDto: TaskDto
    ): Response<Unit>

    @PUT("/task")
    suspend fun updateTask(
        @Body taskDto: TaskDto
    ): Response<Unit>

    @GET("/task")
    suspend fun getTask(
        @Query("taskId")
        taskId: String
    ): Response<TaskDto>

    @DELETE("/task")
    suspend fun deleteTask(
        @Query("taskId")
        taskId: String
    ): Response<Unit>
}
