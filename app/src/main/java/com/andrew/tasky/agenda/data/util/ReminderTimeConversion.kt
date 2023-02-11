package com.andrew.tasky.agenda.data.util

import android.util.Log
import com.andrew.tasky.agenda.util.ReminderTime
import java.time.*

object ReminderTimeConversion {
    fun toEnum(startTimeEpochMilli: Long, remindAtEpochMilli: Long): ReminderTime {
        val startTime = startTimeEpochMilli.toLocalDateTime()
        val remindAtTime = remindAtEpochMilli.toLocalDateTime()
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

    fun toEpochMilli(startLocalDateTime: LocalDateTime, reminderTime: ReminderTime): Long {
        return when (reminderTime) {
            ReminderTime.TEN_MINUTES_BEFORE ->
                startLocalDateTime.minusMinutes(10).toZonedEpochMilli()
            ReminderTime.THIRTY_MINUTES_BEFORE ->
                startLocalDateTime.minusMinutes(30).toZonedEpochMilli()
            ReminderTime.ONE_HOUR_BEFORE ->
                startLocalDateTime.minusHours(1).toZonedEpochMilli()
            ReminderTime.SIX_HOURS_BEFORE ->
                startLocalDateTime.minusHours(6).toZonedEpochMilli()
            ReminderTime.ONE_DAY_BEFORE ->
                startLocalDateTime.minusDays(1).toZonedEpochMilli()
        }
    }
}
