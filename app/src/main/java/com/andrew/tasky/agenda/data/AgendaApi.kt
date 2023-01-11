package com.andrew.tasky.agenda.data

import okhttp3.MultipartBody
import retrofit2.http.*

interface AgendaApi {

    @GET("/agenda")
    suspend fun getAgendaItems(
        @Query("timezone")
        timezone: String,
        @Query("time")
        time: Long = System.currentTimeMillis()
    ): AgendaResponse

    @POST("/syncAgenda")
    suspend fun syncAgendaItems(): SyncAgendaResponse

    @Multipart
    @POST("/event")
    suspend fun createEvent(
        @Part eventData: MultipartBody.Part,
        @Part photoData: List<MultipartBody.Part>
    ): Event

    @GET("/event")
    suspend fun getEvent(
        @Query("eventId")
        eventId: String
    ): Event

    @DELETE("/event")
    suspend fun deleteEvent(
        @Query("eventId")
        eventId: String
    )

    @Multipart
    @PUT("/event")
    suspend fun updateEvent(
        @Part eventData: MultipartBody.Part,
        @Part photoData: List<MultipartBody.Part>
    ): Event

    @GET("/attendee")
    suspend fun getAttendee(
        @Query("email")
        email: String
    ): GetAttendeeResponse

    @DELETE("/attendee")
    suspend fun deleteAttendee(
        @Query("eventId")
        eventId: String
    )

    @POST("/task")
    suspend fun createTask(
        @Body task: Task
    )

    @PUT("/task")
    suspend fun updateTask(
        @Body task: Task
    )

    @GET("/task")
    suspend fun getTask(
        @Query("taskId")
        taskId: String
    ): Task

    @DELETE("/task")
    suspend fun deleteTask(
        @Query("taskId")
        taskId: String
    )

    @POST("/reminder")
    suspend fun createReminder(
        @Body reminderResponse: Reminder
    )

    @PUT("/reminder")
    suspend fun updateReminder(
        @Body reminder: Reminder
    )

    @GET("/reminder")
    suspend fun getReminder(
        @Query("reminderId")
        reminderId: String
    ): Reminder

    @DELETE("/reminder")
    suspend fun deleteReminder(
        @Query("reminderId")
        reminderId: String
    )
}
