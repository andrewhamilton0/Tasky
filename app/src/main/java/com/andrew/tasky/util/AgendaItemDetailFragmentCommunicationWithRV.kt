package com.andrew.tasky.util

import com.andrew.tasky.domain.Attendee

interface AgendaItemDetailFragmentCommunicationWithRV {

    fun openPhoto(index: Int){

    }

    fun addNewPhoto(){

    }

    fun deleteAttendee(attendee: Attendee){

    }

}