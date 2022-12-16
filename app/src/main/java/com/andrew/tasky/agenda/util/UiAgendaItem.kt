package com.andrew.tasky.agenda.util

import com.andrew.tasky.agenda.domain.models.AgendaItem

sealed interface UiAgendaItem {
    data class Item(val agendaItem: AgendaItem) : UiAgendaItem
    object TimeNeedle : UiAgendaItem
}
