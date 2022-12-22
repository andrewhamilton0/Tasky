package com.andrew.tasky.agenda.util

import java.time.LocalDate

sealed interface DateType {
    object Yesterday : DateType
    object Today : DateType
    object Tomorrow : DateType
    data class FullDate(val date: LocalDate) : DateType
}
