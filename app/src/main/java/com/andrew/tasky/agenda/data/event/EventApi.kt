package com.andrew.tasky.agenda.data.event

import com.andrew.tasky.agenda.data.networkmodels.EventDto
import com.andrew.tasky.agenda.data.networkmodels.GetAttendeeResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface EventApi {
    @Multipart
    @POST("/event")
    suspend fun createEvent(
        @Part eventData: MultipartBody.Part,
        @Part photoData: List<MultipartBody.Part>
    ): Response<EventDto>

    @GET("/event")
    suspend fun getEvent(
        @Query("eventId")
        eventId: String
    ): Response<EventDto>

    @DELETE("/event")
    suspend fun deleteEvent(
        @Query("eventId")
        eventId: String
    ): Response<Unit>

    @Multipart
    @PUT("/event")
    suspend fun updateEvent(
        @Part eventData: MultipartBody.Part,
        @Part photoData: List<MultipartBody.Part>
    ): Response<EventDto>

    @GET("/attendee")
    suspend fun getAttendee(
        @Query("email")
        email: String
    ): Response<GetAttendeeResponse>

    @DELETE("/attendee")
    suspend fun deleteAttendee(
        @Query("eventId")
        eventId: String
    ): Response<Unit>
}
