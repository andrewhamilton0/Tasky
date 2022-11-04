package com.andrew.tasky.presentation.adapters

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.andrew.tasky.R
import com.andrew.tasky.databinding.ItemAgendaBinding
import com.andrew.tasky.domain.AgendaItem
import com.andrew.tasky.util.AgendaItemActions
import com.andrew.tasky.util.AgendaItemType
import java.time.format.DateTimeFormatter

class AgendaItemAdapter(
    private var agendaItems: List<AgendaItem>,
    private val onAgendaItemOptionClick: (AgendaItem, AgendaItemActions) -> Unit
) : RecyclerView.Adapter<AgendaItemAdapter.AgendaItemViewHolder>() {

    inner class AgendaItemViewHolder(val binding: ItemAgendaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgendaItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemAgendaBinding.inflate(layoutInflater, parent, false)
        return AgendaItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AgendaItemViewHolder, position: Int) {
        holder.binding.apply {
            agendaItemTitle.text = agendaItems[position].title
            agendaItemDescription.text = agendaItems[position].description
            agendaItemDate.text = (
                agendaItems[position].startDateAndTime
                    .format(DateTimeFormatter.ofPattern("MMM d, HH:mm")) +
                    (
                        agendaItems[position].endDateAndTime
                            ?.format(DateTimeFormatter.ofPattern(" - MMM d, HH:mm")) ?: ""
                        )
                )

            doneButton.setOnClickListener {
                agendaItems[position].isDone = true
            }

            agendaItemCard.setOnClickListener {
                onAgendaItemOptionClick(agendaItems[position], AgendaItemActions.OPEN)
            }

            optionsButton.setOnClickListener { view ->
                val popupMenu = PopupMenu(optionsButton.context, view)
                popupMenu.inflate(R.menu.menu_agenda_item_actions)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.open -> {
                            val actionOption = AgendaItemActions.OPEN
                            onAgendaItemOptionClick(agendaItems[position], actionOption)
                            true
                        }
                        R.id.edit -> {
                            val actionOption = AgendaItemActions.EDIT
                            onAgendaItemOptionClick(agendaItems[position], actionOption)
                            true
                        }
                        R.id.delete -> {
                            val actionOption = AgendaItemActions.DELETE
                            onAgendaItemOptionClick(agendaItems[position], actionOption)
                            true
                        }
                        else -> true
                    }
                }
                popupMenu.show()
            }

            if (agendaItems[position].isDone) {
                doneButton.setImageResource(R.drawable.task_done_circle)
                agendaItemTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                doneButton.setImageResource(R.drawable.ic_undone_circle)
                agendaItemTitle.paintFlags = Paint.ANTI_ALIAS_FLAG
            }

            when (agendaItems[position].type) {
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

    override fun getItemCount(): Int {
        return agendaItems.size
    }
}
