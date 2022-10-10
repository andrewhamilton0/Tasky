package com.andrew.tasky.domain

import com.andrew.tasky.data.AgendaItem
import com.andrew.tasky.util.AgendaItemType
import java.time.LocalDate
import java.time.LocalDateTime

object AgendaItems {

    private val agendaItemList = mutableListOf(
        AgendaItem(
            AgendaItemType.EVENT, false, "Meeting",
            "Project meeting with Abby", LocalDateTime.of(2022, 10, 8, 8, 22),
            LocalDateTime.of(2022, 10, 8, 8, 32)
        ),
        AgendaItem(
            AgendaItemType.REMINDER, false, "Take out trash",
            "Take trash from inside to outside", LocalDateTime.of(2022, 11, 7, 8, 37)
        ),
        AgendaItem(
            AgendaItemType.TASK, true, "Code",
            "Finish coding lesson", LocalDateTime.of(2022, 10, 7, 9, 22)
        ),
        AgendaItem(
            AgendaItemType.EVENT, false, "Run",
            "Go for an evening run", LocalDateTime.of(2022, 10, 7, 12, 22),
            LocalDateTime.of(2022, 10, 7, 14, 22)
        )
    )

    //Returns a sorted list of agendaItems by start time and only those of dateSelected
    fun sortByDateSelected(dateSelected: LocalDate): MutableList<AgendaItem>{
        val agendaItemListSorted = mutableListOf<AgendaItem>()
        agendaItemList.sortedBy{ agendaItem -> agendaItem.startDateAndTime }
            .forEach{if (it.startDateAndTime.toLocalDate() == dateSelected){
                agendaItemListSorted.add(it)}}
        return agendaItemListSorted
    }
}