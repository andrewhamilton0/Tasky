package com.andrew.tasky.agenda.presentation.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andrew.tasky.R
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.util.UiAgendaItem
import com.andrew.tasky.databinding.ItemAgendaBinding
import com.andrew.tasky.databinding.ItemTimeNeedleBinding
import java.time.format.DateTimeFormatter

class AgendaItemAdapter(
    private val onAgendaItemCardClick: (AgendaItem) -> Unit,
    private val onAgendaItemOptionClick: (AgendaItem, View) -> Unit,
    // private val onDoneButtonClick: (AgendaItem) -> Unit
) : ListAdapter<UiAgendaItem, RecyclerView.ViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<UiAgendaItem>() {
        private const val TYPE_TIME_NEEDLE = 0
        private const val TYPE_AGENDA_ITEM = 1

        override fun areItemsTheSame(oldItem: UiAgendaItem, newItem: UiAgendaItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: UiAgendaItem, newItem: UiAgendaItem): Boolean {
            return oldItem == newItem
        }
    }

    inner class AgendaItemViewHolder(val binding: ItemAgendaBinding) :
        RecyclerView.ViewHolder(binding.root)
    inner class TimeNeedleViewHolder(val binding: ItemTimeNeedleBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_TIME_NEEDLE -> TimeNeedleViewHolder(
                ItemTimeNeedleBinding.inflate(layoutInflater, parent, false)
            )
            TYPE_AGENDA_ITEM -> AgendaItemViewHolder(
                ItemAgendaBinding.inflate(layoutInflater, parent, false)
            )
            else -> throw java.lang.IllegalArgumentException("Invalid ViewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentList[position] is UiAgendaItem.Item) {
            TYPE_AGENDA_ITEM
        } else {
            TYPE_TIME_NEEDLE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AgendaItemViewHolder -> {
                holder.binding.apply {

                    val item = currentList[position] as UiAgendaItem.Item
                    val agendaItem = item.agendaItem

                    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, HH:mm")

                    when (agendaItem) {
                        is AgendaItem.Event -> {
                            agendaItemTitle.text = agendaItem.title
                            agendaItemDescription.text = agendaItem.description

                            if (agendaItem.isDone) {
                                doneButton.setImageResource(R.drawable.task_done_circle)
                                agendaItemTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                            } else {
                                doneButton.setImageResource(R.drawable.ic_undone_circle)
                                agendaItemTitle.paintFlags = Paint.ANTI_ALIAS_FLAG
                            }
                            val formattedStartDate = agendaItem.startDateAndTime
                                .format(dateFormatter)
                            val formattedEndDate = agendaItem.endDateAndTime.format(dateFormatter)
                            val startAndEndDatesText = "$formattedStartDate - $formattedEndDate"
                            agendaItemDate.text = startAndEndDatesText

                            agendaItemCard.setCardBackgroundColor(
                                ContextCompat.getColor(holder.itemView.context, R.color.light_green)
                            )
                            doneButton.setColorFilter(
                                ContextCompat.getColor(holder.itemView.context, R.color.black)
                            )
                            optionsButton.setTextColor(
                                ContextCompat.getColor(holder.itemView.context, R.color.black)
                            )
                            agendaItemTitle.setTextColor(
                                ContextCompat.getColor(holder.itemView.context, R.color.black)
                            )
                            agendaItemDescription.setTextColor(
                                ContextCompat.getColor(holder.itemView.context, R.color.black)
                            )
                            agendaItemDate.setTextColor(
                                ContextCompat.getColor(holder.itemView.context, R.color.black)
                            )
                        }
                        is AgendaItem.Reminder -> {
                            agendaItemTitle.text = agendaItem.title
                            agendaItemDescription.text = agendaItem.description

                            if (agendaItem.isDone) {
                                doneButton.setImageResource(R.drawable.task_done_circle)
                                agendaItemTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                            } else {
                                doneButton.setImageResource(R.drawable.ic_undone_circle)
                                agendaItemTitle.paintFlags = Paint.ANTI_ALIAS_FLAG
                            }

                            val formattedStartedDate = agendaItem.startDateAndTime
                                .format(dateFormatter)
                            agendaItemDate.text = formattedStartedDate

                            agendaItemCard.setCardBackgroundColor(
                                ContextCompat.getColor(holder.itemView.context, R.color.light_2)
                            )
                            doneButton.setColorFilter(
                                ContextCompat.getColor(holder.itemView.context, R.color.black)
                            )
                            optionsButton.setTextColor(
                                ContextCompat.getColor(holder.itemView.context, R.color.black)
                            )
                            agendaItemTitle.setTextColor(
                                ContextCompat.getColor(holder.itemView.context, R.color.black)
                            )
                            agendaItemDescription.setTextColor(
                                ContextCompat.getColor(holder.itemView.context, R.color.black)
                            )
                            agendaItemDate.setTextColor(
                                ContextCompat.getColor(holder.itemView.context, R.color.black)
                            )
                        }
                        is AgendaItem.Task -> {
                            agendaItemTitle.text = agendaItem.title
                            agendaItemDescription.text = agendaItem.description

                            if (agendaItem.isDone) {
                                doneButton.setImageResource(R.drawable.task_done_circle)
                                agendaItemTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                            } else {
                                doneButton.setImageResource(R.drawable.ic_undone_circle)
                                agendaItemTitle.paintFlags = Paint.ANTI_ALIAS_FLAG
                            }

                            val formattedStartedDate = agendaItem.startDateAndTime
                                .format(dateFormatter)
                            agendaItemDate.text = formattedStartedDate

                            agendaItemCard.setCardBackgroundColor(
                                ContextCompat.getColor(holder.itemView.context, R.color.green)
                            )
                            doneButton.setColorFilter(
                                ContextCompat.getColor(holder.itemView.context, R.color.white)
                            )
                            optionsButton.setTextColor(
                                ContextCompat.getColor(holder.itemView.context, R.color.white)
                            )
                            agendaItemTitle.setTextColor(
                                ContextCompat.getColor(holder.itemView.context, R.color.white)
                            )
                            agendaItemDescription.setTextColor(
                                ContextCompat.getColor(holder.itemView.context, R.color.white)
                            )
                            agendaItemDate.setTextColor(
                                ContextCompat.getColor(holder.itemView.context, R.color.white)
                            )
                        }
                    }

                    // doneButton.setOnClickListener {
                    //    onDoneButtonClick(agendaItem)
                    // }

                    agendaItemCard.setOnClickListener {
                        onAgendaItemCardClick(agendaItem)
                    }

                    optionsButton.setOnClickListener { view ->
                        onAgendaItemOptionClick(agendaItem, view)
                    }
                }
            }
        }
    }
}
