package com.andrew.tasky.presentation.adapter

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andrew.tasky.R
import com.andrew.tasky.data.AgendaItem
import com.andrew.tasky.databinding.ItemAgendaBinding
import com.andrew.tasky.util.AgendaItemType

class AgendaItemAdapter(
    var agendaItems: List<AgendaItem>
): RecyclerView.Adapter<AgendaItemAdapter.AgendaItemViewHolder>() {

    inner class AgendaItemViewHolder(val binding: ItemAgendaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgendaItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemAgendaBinding.inflate(layoutInflater, parent, false)
        return AgendaItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AgendaItemViewHolder, position: Int) {
        holder.binding.apply {
            agendaItemTitle.text = agendaItems[position].title
            agendaItemDescription.text = agendaItems[position].description
            agendaItemDate.text = agendaItems[position].fromDate + ", " + agendaItems[position].fromTime

            doneButton.setOnClickListener{
                agendaItems[position].isDone = true
                doneButton.setImageResource(R.drawable.task_done_circle)
                agendaItemTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }

            if (agendaItems[position].isDone){
                doneButton.setImageResource(R.drawable.task_done_circle)
                agendaItemTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }
            else{
                doneButton.setImageResource(R.drawable.task_undone_circle)
                agendaItemTitle.paintFlags = Paint.ANTI_ALIAS_FLAG
            }

            when(agendaItems[position].type){
                AgendaItemType.TASK -> {
                    agendaItemCard.setCardBackgroundColor(Color.parseColor("#259f70"))
                    doneButton.setColorFilter(Color.parseColor("#FFeeeeee"))
                    optionsButton.setTextColor(Color.parseColor("#FFeeeeee"))
                    agendaItemTitle.setTextColor(Color.parseColor("#FFeeeeee"))
                    agendaItemDescription.setTextColor(Color.parseColor("#FFeeeeee"))
                    agendaItemDate.setTextColor(Color.parseColor("#FFeeeeee"))
                }
                AgendaItemType.EVENT -> agendaItemCard.setCardBackgroundColor(Color.parseColor("#cced42"))
                AgendaItemType.REMINDER -> agendaItemCard.setCardBackgroundColor(Color.parseColor("#f2f3f7"))
            }
        }
    }

    override fun getItemCount(): Int {
        return agendaItems.size
    }
}