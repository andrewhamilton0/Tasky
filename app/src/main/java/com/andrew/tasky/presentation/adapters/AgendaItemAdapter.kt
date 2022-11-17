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
import com.andrew.tasky.domain.models.AgendaItem
import com.andrew.tasky.util.AgendaItemMenuOption
import com.andrew.tasky.util.AgendaItemType
import java.time.format.DateTimeFormatter

class AgendaItemAdapter(
    private val onAgendaItemOptionClick: (AgendaItem, AgendaItemMenuOption) -> Unit
) : ListAdapter<AgendaItem, AgendaItemAdapter.AgendaItemViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<AgendaItem>() {
        override fun areItemsTheSame(oldItem: AgendaItem, newItem: AgendaItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AgendaItem, newItem: AgendaItem): Boolean {
            return oldItem == newItem
        }
    }

    inner class AgendaItemViewHolder(val binding: ItemAgendaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgendaItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemAgendaBinding.inflate(layoutInflater, parent, false)
        return AgendaItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AgendaItemViewHolder, position: Int) {
        holder.binding.apply {
            agendaItemTitle.text = currentList[position].title
            agendaItemDescription.text = currentList[position].description
            agendaItemDate.text = (
                currentList[position].startDateAndTime
                    .format(DateTimeFormatter.ofPattern("MMM d, HH:mm")) +
                    (
                        currentList[position].endDateAndTime
                            ?.format(DateTimeFormatter.ofPattern(" - MMM d, HH:mm")) ?: ""
                        )
                )

            doneButton.setOnClickListener {
                currentList[position].isDone = true
            }

            agendaItemCard.setOnClickListener {
                onAgendaItemOptionClick(currentList[position], AgendaItemMenuOption.OPEN)
            }

            optionsButton.setOnClickListener { view ->
                val popupMenu = PopupMenu(optionsButton.context, view)
                popupMenu.inflate(R.menu.menu_agenda_item_actions)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.open -> {
                            val actionOption = AgendaItemMenuOption.OPEN
                            onAgendaItemOptionClick(currentList[position], actionOption)
                            true
                        }
                        R.id.edit -> {
                            val actionOption = AgendaItemMenuOption.EDIT
                            onAgendaItemOptionClick(currentList[position], actionOption)
                            true
                        }
                        R.id.delete -> {
                            val actionOption = AgendaItemMenuOption.DELETE
                            onAgendaItemOptionClick(currentList[position], actionOption)
                            true
                        }
                        else -> true
                    }
                }
                popupMenu.show()
            }

            if (currentList[position].isDone) {
                doneButton.setImageResource(R.drawable.task_done_circle)
                agendaItemTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                doneButton.setImageResource(R.drawable.ic_undone_circle)
                agendaItemTitle.paintFlags = Paint.ANTI_ALIAS_FLAG
            }

            when (currentList[position].type) {
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
