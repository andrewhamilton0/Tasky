package com.andrew.tasky.util

import com.andrew.tasky.domain.models.AgendaItem

sealed interface UiAgendaItem {
    data class Item(val agendaItem: AgendaItem) : UiAgendaItem
    object TimeNeedle : UiAgendaItem
}
