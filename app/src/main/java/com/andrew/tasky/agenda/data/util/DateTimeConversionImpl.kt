package com.andrew.tasky.agenda.data.util

import android.icu.util.TimeZone
import com.andrew.tasky.agenda.domain.DateTimeConversion
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class DateTimeConversionImpl : DateTimeConversion {

    override fun localDateTimeToZonedEpochMilli(localDateTime: LocalDateTime): Long {
        return ZonedDateTime.of(
            localDateTime,
            ZoneId.of(TimeZone.getDefault().id)
        ).toInstant().toEpochMilli()
    }

    override fun zonedEpochMilliToLocalDateTime(zonedEpochMilli: Long): LocalDateTime {
        return ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(zonedEpochMilli),
            ZoneId.of(TimeZone.getDefault().id)
        ).toLocalDateTime()
    }
}
