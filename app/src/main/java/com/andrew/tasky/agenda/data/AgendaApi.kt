package com.andrew.tasky.agenda.data

import com.andrew.tasky.agenda.util.Event
import com.andrew.tasky.agenda.util.Reminder
import com.andrew.tasky.agenda.util.Task
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface AgendaApi {

    @GET("/agenda")
    suspend fun getAgendaItems(
        @Query("timezone")
        timeZone: String,
        @Query("time")
        time: Long = System.currentTimeMillis()
    ): Response<AgendaResponse>

    @POST("/syncAgenda")
    suspend fun syncAgendaItems(): Response<SyncAgendaResponse>

    @Multipart
    @POST("/event")
    suspend fun createEvent(
        @Part eventData: MultipartBody.Part,
        @Part photoData: List<MultipartBody.Part>
    ): Response<Event>

    @GET("/event")
    suspend fun getEvent(
        @Query("eventId")
        eventId: String
    ): Response<Event>

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
    ): Response<Event>

    @GET("/attendee")
    suspend fun getAttendee(
        @Query("email")
        email: String
    ): Response<GetAttendeeResponse>

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
    ): Response<Task>

    @DELETE("/task")
    suspend fun deleteTask(
        @Query("taskId")
        taskId: String
    )

    @POST("/reminder")
    suspend fun createReminder(
        @Body reminder: Reminder
    )

    @PUT("/reminder")
    suspend fun updateReminder(
        @Body reminder: Reminder
    )

    @GET("/reminder")
    suspend fun getReminder(
        @Query("reminderId")
        reminderId: String
    ): Response<Reminder>

    @DELETE("/reminder")
    suspend fun deleteReminder(
        @Query("reminderId")
        reminderId: String
    )
}
