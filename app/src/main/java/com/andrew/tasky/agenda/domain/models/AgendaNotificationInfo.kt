package com.andrew.tasky.agenda.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AgendaNotificationInfo(
    val title: String,
    val description: String,
    val id: String,
    val notificationChannel: String,
    val navDestinationId: Int,
    val notificationZonedMilliTime: Long
) : Parcelable
