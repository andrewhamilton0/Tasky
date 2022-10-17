package com.andrew.tasky.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.andrew.tasky.databinding.ItemAttendeeBinding

class AttendeeItemAdapter(
    private var attendees: List<String>
): RecyclerView.Adapter<AttendeeItemAdapter.AttendeeItemViewHolder>() {

    inner class AttendeeItemViewHolder(val binding: ItemAttendeeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendeeItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemAttendeeBinding.inflate(layoutInflater, parent, false)
        return AttendeeItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttendeeItemViewHolder, position: Int) {
        holder.binding.apply {

            //Todo() setup creator text view for only the creator
            attendeeFullNameTextView.text = attendees[position]
            if(position == 0){
                deleteAttendeeButton.isVisible = false
                creatorTextView.isVisible = true
            }
            else{
                deleteAttendeeButton.isVisible = true
                creatorTextView.isVisible = false
            }

            if(attendees[position].contains(" ")){
                val initials = attendees[position]
                    .replace("^\\s*([a-zA-Z]).*\\s+([a-zA-Z])\\S+$"
                        .toRegex(), "$1$2").uppercase()

                attendeeInitialsTextView.text = initials
            }
            else{
                val initials = attendees[position].take(2).uppercase()
                attendeeInitialsTextView.text = initials
            }
        }
    }

    override fun getItemCount(): Int {
        return attendees.size
    }
}