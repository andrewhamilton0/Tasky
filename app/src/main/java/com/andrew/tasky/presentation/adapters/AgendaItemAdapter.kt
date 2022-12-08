package com.andrew.tasky.presentation.adapters

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andrew.tasky.R
import com.andrew.tasky.databinding.ItemAgendaBinding
import com.andrew.tasky.databinding.ItemTimeNeedleBinding
import com.andrew.tasky.domain.models.AgendaItem
import com.andrew.tasky.util.AgendaItemMenuOption
import com.andrew.tasky.util.AgendaItemType
import com.andrew.tasky.util.UiAgendaItem
import java.time.format.DateTimeFormatter

class AgendaItemAdapter(
    private val onAgendaItemOptionClick: (AgendaItem, AgendaItemMenuOption) -> Unit
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
                    agendaItemTitle.text = agendaItem.title
                    agendaItemDescription.text = agendaItem.description
                    agendaItemDate.text = (
                        agendaItem.startDateAndTime
                            .format(DateTimeFormatter.ofPattern("MMM d, HH:mm")) +
                            (
                                agendaItem.endDateAndTime
                                    ?.format(DateTimeFormatter.ofPattern(" - MMM d, HH:mm")) ?: ""
                                )
                        )

                    doneButton.setOnClickListener {
                        agendaItem.isDone = true
                    }

                    agendaItemCard.setOnClickListener {
                        onAgendaItemOptionClick(agendaItem, AgendaItemMenuOption.OPEN)
                    }

                    optionsButton.setOnClickListener { view ->
                        val popupMenu = PopupMenu(optionsButton.context, view)
                        popupMenu.inflate(R.menu.menu_agenda_item_actions)
                        popupMenu.setOnMenuItemClickListener { menuItem ->
                            when (menuItem.itemId) {
                                R.id.open -> {
                                    val actionOption = AgendaItemMenuOption.OPEN
                                    onAgendaItemOptionClick(agendaItem, actionOption)
                                    true
                                }
                                R.id.edit -> {
                                    val actionOption = AgendaItemMenuOption.EDIT
                                    onAgendaItemOptionClick(agendaItem, actionOption)
                                    true
                                }
                                R.id.delete -> {
                                    val actionOption = AgendaItemMenuOption.DELETE
                                    onAgendaItemOptionClick(agendaItem, actionOption)
                                    true
                                }
                                else -> true
                            }
                        }
                        popupMenu.show()
                    }

                    if (agendaItem.isDone) {
                        doneButton.setImageResource(R.drawable.task_done_circle)
                        agendaItemTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    } else {
                        doneButton.setImageResource(R.drawable.ic_undone_circle)
                        agendaItemTitle.paintFlags = Paint.ANTI_ALIAS_FLAG
                    }

                    when (agendaItem.type) {
                        AgendaItemType.TASK -> {
                            agendaItemCard.setCardBackgroundColor(Color.parseColor("#259f70"))
                            doneButton.setColorFilter(Color.parseColor("#FFeeeeee"))
                            optionsButton.setTextColor(Color.parseColor("#FFeeeeee"))
                            agendaItemTitle.setTextColor(Color.parseColor("#FFeeeeee"))
                            agendaItemDescription.setTextColor(Color.parseColor("#FFeeeeee"))
                            agendaItemDate.setTextColor(Color.parseColor("#FFeeeeee"))
                        }
                        AgendaItemType.EVENT -> agendaItemCard.setCardBackgroundColor(
                            Color.parseColor("#cced42")
                        )
                        AgendaItemType.REMINDER -> agendaItemCard.setCardBackgroundColor(
                            Color.parseColor("#f2f3f7")
                        )
                    }
                }
            }
        }
    }
}
