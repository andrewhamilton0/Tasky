package com.andrew.tasky.agenda.data.task

import retrofit2.http.*

interface TaskApi {
    @POST("/task")
    suspend fun createTask(
        @Body taskDto: TaskDto
    )

    @PUT("/task")
    suspend fun updateTask(
        @Body taskDto: TaskDto
    )

    @GET("/task")
    suspend fun getTask(
        @Query("taskId")
        taskId: String
    ): TaskDto

    @DELETE("/task")
    suspend fun deleteTask(
        @Query("taskId")
        taskId: String
    )
}
