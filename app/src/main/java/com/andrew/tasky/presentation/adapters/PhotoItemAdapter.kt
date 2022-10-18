package com.andrew.tasky.presentation.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andrew.tasky.databinding.ItemPhotoAdapterCardBinding
import com.andrew.tasky.util.AgendaItemDetailFragmentCommunicationWithRV

class PhotoItemAdapter(
    private var photos: List<Uri>,
    private val listener: AgendaItemDetailFragmentCommunicationWithRV
):RecyclerView.Adapter<PhotoItemAdapter.PhotoItemViewHolder>() {

    inner class PhotoItemViewHolder(val binding: ItemPhotoAdapterCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemPhotoAdapterCardBinding.inflate(layoutInflater)
        return PhotoItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoItemViewHolder, position: Int) {
        holder.binding.apply {
            if (position != photos.size) {
                image.setImageURI(photos[position])
                holder.itemView.setOnClickListener {
                    listener.openPhoto(position)
                }
            }
            if (position == 10){
                holder.itemView.layoutParams = RecyclerView.LayoutParams(0,0)
            }
            else if(position == photos.size){
                holder.itemView.setOnClickListener {
                    listener.addNewPhoto()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return photos.size+1
    }
}