package com.andrew.tasky.agenda.data.agenda

import com.andrew.tasky.agenda.data.*
import com.andrew.tasky.agenda.data.networkmodels.AgendaResponse
import com.andrew.tasky.agenda.data.networkmodels.SyncAgendaRequest
import retrofit2.Response
import retrofit2.http.*

interface AgendaApi {

    @GET("/agenda")
    suspend fun getAgendaItems(
        @Query("timezone")
        timezone: String,
        @Query("time")
        time: Long
    ): Response<AgendaResponse>

    @POST("/syncAgenda")
    suspend fun syncAgendaItems(
        @Body request: SyncAgendaRequest
    ): Response<Unit>

    @GET("/fullAgenda")
    suspend fun getFullAgenda(): Response<AgendaResponse>
}
