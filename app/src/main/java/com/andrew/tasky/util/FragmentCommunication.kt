package com.andrew.tasky.util

import com.andrew.tasky.domain.AgendaItem

interface FragmentCommunication {

    fun respond(agendaItem: AgendaItem, actionOption: AgendaItemActionOptions){

    }
}