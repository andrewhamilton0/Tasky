package com.andrew.tasky.agenda.data

import com.andrew.tasky.agenda.util.ReminderTime
import java.time.*
import kotlin.time.Duration.Companion.minutes

object ReminderTimeConversion {
    fun toEnum(startTimeEpochSecond: Long, remindAtEpochSecond: Long): ReminderTime {
        val startTime = LocalDateTime.ofEpochSecond(startTimeEpochSecond, 0, null)
        val remindAtTime = LocalDateTime.ofEpochSecond(remindAtEpochSecond, 0, null)
        val period = Period.between(
            remindAtTime.toLocalDate(),
            startTime.toLocalDate()
        )
        val duration = Duration.between(
            remindAtTime.toLocalTime(),
            startTime.toLocalTime()
        )
        return if (period.days == 1) {
            ReminderTime.ONE_DAY_BEFORE
        } else if (duration.seconds.minutes.inWholeHours.toInt() == 6) {
            ReminderTime.SIX_HOURS_BEFORE
        } else if (duration.seconds.minutes.inWholeHours.toInt() == 1) {
            ReminderTime.ONE_HOUR_BEFORE
        } else if (duration.seconds.minutes.inWholeMinutes.toInt() == 30) {
            ReminderTime.THIRTY_MINUTES_BEFORE
        } else {
            ReminderTime.TEN_MINUTES_BEFORE
        }
    }

    fun toEpochSecond(startTimeEpochSecond: Long, reminderTime: ReminderTime): Long {
        val startTime = LocalDateTime.ofEpochSecond(startTimeEpochSecond, 0, null)
        return when (reminderTime) {
            ReminderTime.TEN_MINUTES_BEFORE ->
                startTime.minusMinutes(10).toEpochSecond(null)
            ReminderTime.THIRTY_MINUTES_BEFORE ->
                startTime.minusMinutes(30).toEpochSecond(null)
            ReminderTime.ONE_HOUR_BEFORE ->
                startTime.minusHours(1).toEpochSecond(null)
            ReminderTime.SIX_HOURS_BEFORE ->
                startTime.minusHours(6).toEpochSecond(null)
            ReminderTime.ONE_DAY_BEFORE ->
                startTime.minusDays(1).toEpochSecond(null)
        }
    }
}
