package com.andrew.tasky.domain

import com.andrew.tasky.util.AgendaItemType
import com.andrew.tasky.util.ReminderTimes
import java.time.LocalDate
import java.time.LocalDateTime

object AgendaItems {

    private var agendaItemList = listOf(
        AgendaItem(
            AgendaItemType.EVENT,
            false,
            "Meeting",
            "Project meeting with Abby",
            LocalDateTime.of(2022, 10, 8, 8, 22),
            LocalDateTime.of(2022, 10, 8, 8, 32),
            ReminderTimes.SIX_HOURS_BEFORE
        ),
        AgendaItem(
            AgendaItemType.REMINDER,
            false,
            "Take out trash",
            "Take trash from inside to outside",
            LocalDateTime.of(2022, 11, 7, 8, 37),
            reminderTime = ReminderTimes.ONE_DAY_BEFORE
        ),
        AgendaItem(
            AgendaItemType.TASK,
            true,
            "Code",
            "Finish coding lesson",
            LocalDateTime.of(2022, 10, 7, 9, 22),
            reminderTime = ReminderTimes.THIRTY_MINUTES_BEFORE
        ),
        AgendaItem(
            AgendaItemType.EVENT,
            false,
            "Run",
            "Go for an evening run",
            LocalDateTime.of(2022, 10, 7, 12, 22),
            LocalDateTime.of(2022, 10, 7, 14, 22),
            ReminderTimes.TEN_MINUTES_BEFORE
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

    fun addAgendaItem(newAgendaItem: AgendaItem){
        val updatedAgendaItemList: List<AgendaItem> = agendaItemList.toMutableList().apply {
            add(newAgendaItem)
        }
        agendaItemList = updatedAgendaItemList
    }

    fun replaceAgendaItem(newAgendaItem: AgendaItem, oldAgendaItem: AgendaItem){
        val updatedAgendaItemList: List<AgendaItem> = agendaItemList.toMutableList().apply {
            this[this.indexOf(oldAgendaItem)] = newAgendaItem
        }
        agendaItemList = updatedAgendaItemList
    }

    fun deleteAgendaItem(agendaItem: AgendaItem){
        val updatedAgendaItemList: List<AgendaItem> = agendaItemList.toMutableList().apply {
            remove(agendaItem)
        }
        agendaItemList = updatedAgendaItemList
    }
}