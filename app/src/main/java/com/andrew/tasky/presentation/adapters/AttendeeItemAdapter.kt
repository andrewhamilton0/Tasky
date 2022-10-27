package com.andrew.tasky.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.andrew.tasky.databinding.ItemAttendeeBinding
import com.andrew.tasky.domain.Attendee
import com.andrew.tasky.domain.AttendeeType
import com.andrew.tasky.domain.StringToInitials
import com.andrew.tasky.util.AgendaItemDetailFragmentCommunicationWithRV

class AttendeeItemAdapter(
    private var attendees: List<Attendee>,
    private val listener: AgendaItemDetailFragmentCommunicationWithRV
): RecyclerView.Adapter<AttendeeItemAdapter.AttendeeItemViewHolder>() {

    inner class AttendeeItemViewHolder(val binding: ItemAttendeeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendeeItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemAttendeeBinding.inflate(layoutInflater, parent, false)
        return AttendeeItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttendeeItemViewHolder, position: Int) {
        holder.binding.apply {

            //Adds and removes deleteAttendeeButtonView, and creatorTV to appropriate attendee
            if(attendees[position].attendeeType == AttendeeType.CREATOR) {
                deleteAttendeeButton.isVisible = false
                creatorTextView.isVisible = true
            }
            else{
                deleteAttendeeButton.isVisible = true
                creatorTextView.isVisible = false
            }

            //Adds full name to card
            attendeeFullNameTextView.text = attendees[position].name

            //Adds initials to card
            attendeeInitialsTextView.text = StringToInitials().convertStringToInitials(attendees[position].name)

            //Adds on click listener to delete button
            deleteAttendeeButton.setOnClickListener {
                listener.deleteAttendee(attendees[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return attendees.size
    }
}