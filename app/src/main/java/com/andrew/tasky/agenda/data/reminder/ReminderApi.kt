package com.andrew.tasky.agenda.data.reminder

import retrofit2.http.*

interface ReminderApi {
    @POST("/reminder")
    suspend fun createReminder(
        @Body reminderDto: ReminderDto
    )

    @PUT("/reminder")
    suspend fun updateReminder(
        @Body reminderDto: ReminderDto
    )

    @GET("/reminder")
    suspend fun getReminder(
        @Query("reminderId")
        reminderId: String
    ): ReminderDto

    @DELETE("/reminder")
    suspend fun deleteReminder(
        @Query("reminderId")
        reminderId: String
    )
}
