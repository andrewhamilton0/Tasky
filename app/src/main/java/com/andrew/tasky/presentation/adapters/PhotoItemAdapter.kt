package com.andrew.tasky.presentation.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andrew.tasky.databinding.ItemPhotoAdapterCardBinding

class PhotoItemAdapter(
    private var photos: List<Uri>,
    private val onPhotoClick: (Int) -> Unit,
    private val onAddPhotoClick: () -> Unit,
    private val userIsAttendee: Boolean
) : RecyclerView.Adapter<PhotoItemAdapter.PhotoItemViewHolder>() {

    inner class PhotoItemViewHolder(val binding: ItemPhotoAdapterCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemPhotoAdapterCardBinding.inflate(layoutInflater, parent, false)
        return PhotoItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoItemViewHolder, position: Int) {
        holder.binding.apply {
            if (position != photos.size) {
                image.setImageURI(photos[position])
                holder.itemView.setOnClickListener {
                    onPhotoClick(position)
                }
            }

            // Gets rid of add photo after 10 photos, and adds click listener to add photo card
            if (position == 10) {
                holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            } else if (position == photos.size) {
                holder.itemView.setOnClickListener {
                    onAddPhotoClick()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        // If user is attendee and not creator, then user cannot see add photo card
        return if (userIsAttendee) {
            photos.size
        } else {
            photos.size + 1
        }
    }
}
