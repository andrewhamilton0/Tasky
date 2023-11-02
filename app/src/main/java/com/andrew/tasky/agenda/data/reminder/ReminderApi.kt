package com.andrew.tasky.agenda.data.reminder

import com.andrew.tasky.agenda.data.networkModels.ReminderDto
import retrofit2.Response
import retrofit2.http.*

interface ReminderApi {
    @POST("/reminder")
    suspend fun createReminder(
        @Body reminderDto: ReminderDto
    ): Response<Unit>

    @PUT("/reminder")
    suspend fun updateReminder(
        @Body reminderDto: ReminderDto
    ): Response<Unit>

    @GET("/reminder")
    suspend fun getReminder(
        @Query("reminderId")
        reminderId: String
    ): Response<ReminderDto>

    @DELETE("/reminder")
    suspend fun deleteReminder(
        @Query("reminderId")
        reminderId: String
    ): Response<Unit>
}
