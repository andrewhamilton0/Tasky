package com.andrew.tasky.agenda.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andrew.tasky.R
import com.andrew.tasky.agenda.domain.models.CalendarDateItem
import com.andrew.tasky.databinding.ItemMiniCalendarDayBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MiniCalendarAdapter(
    private val onDateClick: (LocalDate) -> Unit
) : ListAdapter<CalendarDateItem, MiniCalendarAdapter.MiniCalendarViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<CalendarDateItem>() {
        override fun areItemsTheSame(
            oldItem: CalendarDateItem,
            newItem: CalendarDateItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: CalendarDateItem,
            newItem: CalendarDateItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    inner class MiniCalendarViewHolder(val binding: ItemMiniCalendarDayBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiniCalendarViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemMiniCalendarDayBinding.inflate(layoutInflater, parent, false)
        return MiniCalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MiniCalendarViewHolder, position: Int) {
        holder.binding.apply {
            dateNumber.text = currentList[position].date.format(
                DateTimeFormatter.ofPattern("d")
            )
            dayOfWeek.text = currentList[position].date.format(
                DateTimeFormatter.ofPattern("eeeee")
            )

            if (currentList[position].isSelected) {
                miniCalendarCard.setCardBackgroundColor(
                    ResourcesCompat.getColor(
                        holder.itemView.context.resources,
                        R.color.orange,
                        null
                    )
                )
            } else {
                miniCalendarCard.setCardBackgroundColor(
                    ResourcesCompat.getColor(
                        holder.itemView.context.resources,
                        R.color.white,
                        null
                    )
                )
            }

            miniCalendarCard.setOnClickListener {
                onDateClick(currentList[position].date)
            }
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }
}
