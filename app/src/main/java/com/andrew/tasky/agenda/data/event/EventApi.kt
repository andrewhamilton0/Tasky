package com.andrew.tasky.agenda.data.event

import okhttp3.MultipartBody
import retrofit2.http.*

interface EventApi {
    @Multipart
    @POST("/event")
    suspend fun createEvent(
        @Part eventData: MultipartBody.Part,
        @Part photoData: List<MultipartBody.Part>
    ): EventDto

    @GET("/event")
    suspend fun getEvent(
        @Query("eventId")
        eventId: String
    ): EventDto

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
    ): EventDto

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
}
