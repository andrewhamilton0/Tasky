package com.andrew.tasky.agenda.data.util

import android.util.Log
import com.andrew.tasky.agenda.util.ReminderTime
import java.time.*
import java.util.TimeZone

object ReminderTimeConversion {
    fun toEnum(startTimeEpochSecond: Long, remindAtEpochSecond: Long): ReminderTime {
        val startTime = ZonedDateTime.ofInstant(
            Instant.ofEpochSecond(startTimeEpochSecond),
            TimeZone.getDefault().toZoneId()
        ).toLocalDateTime()
        val remindAtTime = ZonedDateTime.ofInstant(
            Instant.ofEpochSecond(remindAtEpochSecond),
            TimeZone.getDefault().toZoneId()
        ).toLocalDateTime()
        val period = Period.between(
            remindAtTime.toLocalDate(),
            startTime.toLocalDate()
        )
        val duration = Duration.between(
            remindAtTime.toLocalTime(),
            startTime.toLocalTime()
        )

        return when {
            period.days == 1 ->
                ReminderTime.ONE_DAY_BEFORE
            duration.toHours().toInt() == 6 ->
                ReminderTime.SIX_HOURS_BEFORE
            duration.toHours().toInt() == 1 ->
                ReminderTime.ONE_HOUR_BEFORE
            duration.toMinutes().toInt() == 30 ->
                ReminderTime.THIRTY_MINUTES_BEFORE
            duration.toMinutes().toInt() == 10 ->
                ReminderTime.TEN_MINUTES_BEFORE
            else -> {
                Log.e("ReminderTimeConversion", "No Reminder Time Found or Matched")
                ReminderTime.TEN_MINUTES_BEFORE
            }
        }
    }

    fun toEpochSecond(startLocalDateTime: LocalDateTime, reminderTime: ReminderTime): Long {
        return when (reminderTime) {
            ReminderTime.TEN_MINUTES_BEFORE ->
                ZonedDateTime.of(
                    startLocalDateTime.minusMinutes(10),
                    TimeZone.getDefault().toZoneId()
                ).toEpochSecond()
            ReminderTime.THIRTY_MINUTES_BEFORE ->
                ZonedDateTime.of(
                    startLocalDateTime.minusMinutes(30),
                    TimeZone.getDefault().toZoneId()
                ).toEpochSecond()
            ReminderTime.ONE_HOUR_BEFORE ->
                ZonedDateTime.of(
                    startLocalDateTime.minusHours(1),
                    TimeZone.getDefault().toZoneId()
                ).toEpochSecond()
            ReminderTime.SIX_HOURS_BEFORE ->
                ZonedDateTime.of(
                    startLocalDateTime.minusHours(6),
                    TimeZone.getDefault().toZoneId()
                ).toEpochSecond()
            ReminderTime.ONE_DAY_BEFORE ->
                ZonedDateTime.of(
                    startLocalDateTime.minusDays(1),
                    TimeZone.getDefault().toZoneId()
                ).toEpochSecond()
        }
    }
}
