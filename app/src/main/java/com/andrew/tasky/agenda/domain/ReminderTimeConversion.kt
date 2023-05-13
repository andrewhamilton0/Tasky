package com.andrew.tasky.agenda.domain

import com.andrew.tasky.agenda.util.ReminderTime
import java.time.*

interface ReminderTimeConversion {
    fun toEnum(
        startTimeEpochMilli: Long,
        remindAtEpochMilli: Long,
        dateTimeConversion: DateTimeConversion
    ): ReminderTime

    fun toZonedEpochMilli(
        startLocalDateTime: LocalDateTime,
        reminderTime: ReminderTime,
        dateTimeConversion: DateTimeConversion
    ): Long
}
