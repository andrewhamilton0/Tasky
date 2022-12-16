package com.andrew.tasky.agenda.presentation.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andrew.tasky.agenda.domain.models.Photo
import com.andrew.tasky.databinding.ItemPhotoAdapterCardBinding

class PhotoItemAdapter(
    private val onPhotoClick: (Int) -> Unit,
    private val onAddPhotoClick: () -> Unit,
    private val userIsAttendee: Boolean
) : ListAdapter<Photo, PhotoItemAdapter.PhotoItemViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem == newItem
        }
    }

    inner class PhotoItemViewHolder(val binding: ItemPhotoAdapterCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemPhotoAdapterCardBinding.inflate(layoutInflater, parent, false)
        return PhotoItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoItemViewHolder, position: Int) {
        holder.binding.apply {
            if (position != currentList.size) {
                image.setImageURI(Uri.parse(currentList[position].uriString))
                holder.itemView.setOnClickListener {
                    onPhotoClick(position)
                }
            }

            // Gets rid of add photo after 10 photos, and adds click listener to add photo card
            if (position == 10) {
                holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            } else if (position == currentList.size) {
                holder.itemView.setOnClickListener {
                    onAddPhotoClick()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        // If user is attendee and not creator, then user cannot see add photo card
        return if (userIsAttendee) {
            currentList.size
        } else {
            currentList.size + 1
        }
    }
}
