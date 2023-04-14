package com.andrew.tasky.agenda.domain

import android.icu.util.TimeZone
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

fun LocalDateTime.toZonedEpochMilli(): Long {
    return ZonedDateTime.of(
        this,
        ZoneId.of(TimeZone.getDefault().id)
    ).toInstant().toEpochMilli()
}

fun Long.toLocalDateTime(): LocalDateTime {
    return ZonedDateTime.ofInstant(
        Instant.ofEpochMilli(this),
        ZoneId.of(TimeZone.getDefault().id)
    ).toLocalDateTime()
}
