package com.andrew.tasky.agenda.presentation.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andrew.tasky.agenda.domain.models.EventPhoto
import com.andrew.tasky.databinding.ItemPhotoAdapterCardBinding
import com.bumptech.glide.Glide

class PhotoItemAdapter(
    val context: Context,
    private val onPhotoClick: (Int) -> Unit,
    private val onAddPhotoClick: () -> Unit,
    private val userIsAttendee: Boolean
) : ListAdapter<EventPhoto, PhotoItemAdapter.PhotoItemViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<EventPhoto>() {
        override fun areItemsTheSame(oldItem: EventPhoto, newItem: EventPhoto): Boolean {
            return oldItem.key == newItem.key
        }

        override fun areContentsTheSame(oldItem: EventPhoto, newItem: EventPhoto): Boolean {
            return if (oldItem is EventPhoto.Local && newItem is EventPhoto.Local) {
                oldItem.uri == newItem.uri
            } else if (oldItem is EventPhoto.Remote && newItem is EventPhoto.Remote) {
                oldItem.photoUrl == newItem.photoUrl
            } else false
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

            val item = currentList[position]

            if (position != currentList.size) {
                when (item) {
                    is EventPhoto.Local -> {
                        image.setImageURI(Uri.parse(item.uri.toString()))
                        holder.itemView.setOnClickListener {
                            onPhotoClick(position)
                        }
                    }
                    is EventPhoto.Remote -> {
                        Glide.with(context)
                            .load(item.photoUrl)
                            .into(image)
                    }
                }
            } else if (position == 10) {
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
