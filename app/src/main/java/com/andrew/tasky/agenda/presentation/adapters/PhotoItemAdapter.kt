package com.andrew.tasky.agenda.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andrew.tasky.agenda.domain.models.EventPhoto
import com.andrew.tasky.agenda.util.UiEventPhoto
import com.andrew.tasky.databinding.ItemPhotoAdapterCardBinding
import com.bumptech.glide.Glide

class PhotoItemAdapter(
    val context: Context,
    private val onPhotoClick: (Int) -> Unit,
    private val onAddPhotoClick: () -> Unit,
) : ListAdapter<UiEventPhoto, RecyclerView.ViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<UiEventPhoto>() {
        private const val TYPE_ADD_PHOTO = 0
        private const val TYPE_EVENT_PHOTO = 1

        override fun areItemsTheSame(oldItem: UiEventPhoto, newItem: UiEventPhoto): Boolean {
            return if (oldItem is UiEventPhoto.Photo && newItem is UiEventPhoto.Photo) {
                oldItem.eventPhoto.key == newItem.eventPhoto.key
            } else if (oldItem is UiEventPhoto.AddPhoto && newItem is UiEventPhoto.AddPhoto) {
                oldItem == newItem
            } else false
        }

        override fun areContentsTheSame(oldItem: UiEventPhoto, newItem: UiEventPhoto): Boolean {
            return if (oldItem is UiEventPhoto.Photo && newItem is UiEventPhoto.Photo) {
                return if (oldItem.eventPhoto is EventPhoto.Local &&
                    newItem.eventPhoto is EventPhoto.Local
                ) {
                    oldItem.eventPhoto.uri == newItem.eventPhoto.uri
                } else if (oldItem.eventPhoto is EventPhoto.Remote &&
                    newItem.eventPhoto is EventPhoto.Remote
                ) {
                    oldItem.eventPhoto.photoUrl == newItem.eventPhoto.photoUrl
                } else false
            } else oldItem is UiEventPhoto.AddPhoto && newItem is UiEventPhoto.AddPhoto
        }
    }
    inner class PhotoItemViewHolder(val binding: ItemPhotoAdapterCardBinding) :
        RecyclerView.ViewHolder(binding.root)
    inner class AddPhotoViewHolder(val binding: ItemPhotoAdapterCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return if (currentList[position] is UiEventPhoto.AddPhoto) {
            TYPE_ADD_PHOTO
        } else TYPE_EVENT_PHOTO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_ADD_PHOTO -> AddPhotoViewHolder(
                ItemPhotoAdapterCardBinding.inflate(layoutInflater, parent, false)
            )
            TYPE_EVENT_PHOTO -> PhotoItemViewHolder(
                ItemPhotoAdapterCardBinding.inflate(layoutInflater, parent, false)
            )
            else -> throw java.lang.IllegalArgumentException("Invalid ViewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddPhotoViewHolder -> {
                holder.itemView.setOnClickListener {
                    onAddPhotoClick()
                }
            }
            is PhotoItemViewHolder -> {
                holder.binding.apply {
                    val uiEventPhoto = currentList[position] as UiEventPhoto.Photo
                    when (val item = uiEventPhoto.eventPhoto) {
                        is EventPhoto.Local -> {
                            image.setImageURI(item.uri)
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
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }
}
