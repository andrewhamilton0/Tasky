package com.andrew.tasky.agenda.data.util

import android.util.Log
import com.andrew.tasky.agenda.util.ReminderTime
import java.time.*

object ReminderTimeConversion {
    fun toEnum(startTimeEpochSecond: Long, remindAtEpochSecond: Long): ReminderTime {
        val startTime = LocalDateTime.ofEpochSecond(
            startTimeEpochSecond, 0, ZoneOffset.UTC
        )
        val remindAtTime = LocalDateTime.ofEpochSecond(
            remindAtEpochSecond, 0, ZoneOffset.UTC
        )
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

    fun toEpochSecond(startTimeEpochSecond: Long, reminderTime: ReminderTime): Long {
        val startTime = LocalDateTime.ofEpochSecond(
            startTimeEpochSecond, 0, ZoneOffset.UTC
        )
        return when (reminderTime) {
            ReminderTime.TEN_MINUTES_BEFORE ->
                startTime.minusMinutes(10).toEpochSecond(ZoneOffset.UTC)
            ReminderTime.THIRTY_MINUTES_BEFORE ->
                startTime.minusMinutes(30).toEpochSecond(ZoneOffset.UTC)
            ReminderTime.ONE_HOUR_BEFORE ->
                startTime.minusHours(1).toEpochSecond(ZoneOffset.UTC)
            ReminderTime.SIX_HOURS_BEFORE ->
                startTime.minusHours(6).toEpochSecond(ZoneOffset.UTC)
            ReminderTime.ONE_DAY_BEFORE ->
                startTime.minusDays(1).toEpochSecond(ZoneOffset.UTC)
        }
    }
}
