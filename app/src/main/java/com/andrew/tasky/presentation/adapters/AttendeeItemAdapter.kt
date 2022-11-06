package com.andrew.tasky.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.andrew.tasky.databinding.ItemAttendeeBinding
import com.andrew.tasky.domain.Attendee
import com.andrew.tasky.domain.AttendeeType
import com.andrew.tasky.domain.StringToInitials

class AttendeeItemAdapter(
    private var attendees: List<Attendee>,
    private var isAttendee: Boolean,
    private val onDeleteIconClick: (Attendee) -> Unit
) : RecyclerView.Adapter<AttendeeItemAdapter.AttendeeItemViewHolder>() {

    inner class AttendeeItemViewHolder(val binding: ItemAttendeeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendeeItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemAttendeeBinding.inflate(layoutInflater, parent, false)
        return AttendeeItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttendeeItemViewHolder, position: Int) {
        holder.binding.apply {

            val isCreatorHolder = attendees[position].attendeeType == AttendeeType.CREATOR
            deleteAttendeeButton.isVisible = !isCreatorHolder && !isAttendee
            creatorTextView.isVisible = isCreatorHolder

            attendeeFullNameTextView.text = attendees[position].name

            attendeeInitialsTextView.text = StringToInitials
                .convertStringToInitials(attendees[position].name)

            deleteAttendeeButton.setOnClickListener {
                onDeleteIconClick(attendees[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return attendees.size
    }
}
