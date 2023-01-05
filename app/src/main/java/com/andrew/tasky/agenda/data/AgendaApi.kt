package com.andrew.tasky.agenda.data

import com.andrew.tasky.agenda.util.Event
import java.util.TimeZone
import okhttp3.MultipartBody
import retrofit2.Response
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
        timeZone: TimeZone,
        @Query("time")
        time: Long = System.currentTimeMillis()
    ): Response<AgendaResponse>

    @POST("/syncAgenda")
    suspend fun syncAgendaItems(): Response<SyncAgendaResponse>

    @Multipart
    @POST("/event")
    suspend fun createEvent(
        @Part eventData: MultipartBody.Part,
        @Part photoData: MultipartBody.Part
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
        @Part photoData: MultipartBody.Part
    ): Response<Event>
}
