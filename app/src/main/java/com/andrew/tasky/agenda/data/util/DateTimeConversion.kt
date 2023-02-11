package com.andrew.tasky.agenda.data.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.*

fun LocalDateTime.toZonedEpochMilli(): Long {
    return ZonedDateTime.of(
        this,
        TimeZone.getDefault().toZoneId()
    ).toInstant().toEpochMilli()
}

fun Long.toLocalDateTime(): LocalDateTime {
    return ZonedDateTime.ofInstant(
        Instant.ofEpochMilli(this),
        TimeZone.getDefault().toZoneId()
    ).toLocalDateTime()
}
