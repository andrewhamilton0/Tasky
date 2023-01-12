package com.andrew.tasky.agenda.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andrew.tasky.agenda.domain.models.Attendee
import com.andrew.tasky.core.StringToInitials
import com.andrew.tasky.databinding.ItemAttendeeBinding

class AttendeeItemAdapter(
    private var isUserAttendee: Boolean,
    private val onDeleteIconClick: (Attendee) -> Unit
) : ListAdapter<Attendee, AttendeeItemAdapter.AttendeeItemViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<Attendee>() {
        override fun areItemsTheSame(oldItem: Attendee, newItem: Attendee): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Attendee, newItem: Attendee): Boolean {
            return oldItem == newItem
        }
    }

    inner class AttendeeItemViewHolder(val binding: ItemAttendeeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendeeItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemAttendeeBinding.inflate(layoutInflater, parent, false)
        return AttendeeItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttendeeItemViewHolder, position: Int) {
        holder.binding.apply {

            val item = currentList[position]

            deleteAttendeeButton.isVisible = item.isCreator && !isUserAttendee
            creatorTextView.isVisible = item.isCreator

            attendeeFullNameTextView.text = item.fullName
            attendeeInitialsTextView.text = StringToInitials
                .convertStringToInitials(item.fullName)

            deleteAttendeeButton.setOnClickListener {
                onDeleteIconClick(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }
}
