package com.andrew.tasky.agenda.data.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.*

fun localDateTimeToZonedEpochMilli(localDateTime: LocalDateTime): Long {
    return ZonedDateTime.of(
        localDateTime,
        TimeZone.getDefault().toZoneId()
    ).toInstant().toEpochMilli()
}

fun zonedEpochMilliToLocalDateTime(time: Long): LocalDateTime {
    return ZonedDateTime.ofInstant(
        Instant.ofEpochMilli(time),
        TimeZone.getDefault().toZoneId()
    ).toLocalDateTime()
}
