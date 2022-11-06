package com.andrew.tasky.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.andrew.tasky.R
import com.andrew.tasky.databinding.ItemMiniCalendarDayBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MiniCalendarAdapter(
    private val startDate: LocalDate,
    private val calendarSize: Int,
    private val dateSelected: LocalDate,
    private val onHolderClick: (LocalDate) -> Unit
) : RecyclerView.Adapter<MiniCalendarAdapter.MiniCalendarViewHolder>() {

    inner class MiniCalendarViewHolder(val binding: ItemMiniCalendarDayBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiniCalendarViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemMiniCalendarDayBinding.inflate(layoutInflater, parent, false)
        return MiniCalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MiniCalendarViewHolder, position: Int) {
        holder.binding.apply {
            dateNumber.text = startDate.plusDays(position.toLong()).format(
                DateTimeFormatter.ofPattern("d")
            )
            dayOfWeek.text = startDate.plusDays(position.toLong()).format(
                DateTimeFormatter.ofPattern("eeeee")
            )

            if (dateSelected == startDate.plusDays(position.toLong())) {
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

            holder.itemView.setOnClickListener {
                onHolderClick(startDate.plusDays(position.toLong()))
            }
        }
    }

    override fun getItemCount(): Int {
        return calendarSize
    }
}
