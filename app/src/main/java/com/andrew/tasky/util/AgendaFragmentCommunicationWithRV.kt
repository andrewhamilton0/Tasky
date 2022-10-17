package com.andrew.tasky.util

import com.andrew.tasky.domain.AgendaItem

interface AgendaFragmentCommunicationWithRV {

    fun respond(agendaItem: AgendaItem, actionOption: AgendaItemActionOptions){

    }
}