package com.andrew.tasky.agenda.data.reminder

import retrofit2.http.*

interface ReminderApi {
    @POST("/reminder")
    suspend fun createReminder(
        @Body reminderEntity: ReminderEntity
    )

    @PUT("/reminder")
    suspend fun updateReminder(
        @Body reminderEntity: ReminderEntity
    )

    @GET("/reminder")
    suspend fun getReminder(
        @Query("reminderId")
        reminderId: String
    ): ReminderEntity

    @DELETE("/reminder")
    suspend fun deleteReminder(
        @Query("reminderId")
        reminderId: String
    )
}
