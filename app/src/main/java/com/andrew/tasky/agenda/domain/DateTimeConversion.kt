package com.andrew.tasky.agenda.domain

import java.time.LocalDateTime

interface DateTimeConversion {
    fun localDateTimeToZonedEpochMilli(localDateTime: LocalDateTime): Long
    fun zonedEpochMilliToLocalDateTime(zonedEpochMilli: Long): LocalDateTime
}
