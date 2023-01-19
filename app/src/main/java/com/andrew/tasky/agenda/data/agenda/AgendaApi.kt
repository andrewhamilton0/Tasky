package com.andrew.tasky.agenda.data.agenda

import com.andrew.tasky.agenda.data.*
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
}