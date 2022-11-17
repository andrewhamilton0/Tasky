package com.andrew.tasky.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andrew.tasky.databinding.ItemAttendeeBinding
import com.andrew.tasky.domain.Attendee
import com.andrew.tasky.domain.AttendeeType
import com.andrew.tasky.domain.StringToInitials

class AttendeeItemAdapter(
    private var isAttendee: Boolean,
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

            val isCreatorHolder = currentList[position].attendeeType == AttendeeType.CREATOR
            deleteAttendeeButton.isVisible = !isCreatorHolder && !isAttendee
            creatorTextView.isVisible = isCreatorHolder

            attendeeFullNameTextView.text = currentList[position].name

            attendeeInitialsTextView.text = StringToInitials
                .convertStringToInitials(currentList[position].name)

            deleteAttendeeButton.setOnClickListener {
                onDeleteIconClick(currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }
}
