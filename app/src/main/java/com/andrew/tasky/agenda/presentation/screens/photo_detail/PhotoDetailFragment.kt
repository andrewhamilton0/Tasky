package com.andrew.tasky.agenda.presentation.screens.photo_detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.andrew.tasky.R
import com.andrew.tasky.agenda.domain.models.EventPhoto
import com.andrew.tasky.agenda.presentation.screens.event_detail.EventDetailViewModel
import com.andrew.tasky.agenda.util.collectLatestLifecycleFlow
import com.andrew.tasky.databinding.FragmentPhotoDetailBinding
import com.bumptech.glide.Glide

class PhotoDetailFragment : Fragment(R.layout.fragment_photo_detail) {

    private lateinit var binding: FragmentPhotoDetailBinding
    private val viewModel: EventDetailViewModel by hiltNavGraphViewModels(R.id.event_nav)
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPhotoDetailBinding.bind(view)
        navController = Navigation.findNavController(view)

        collectLatestLifecycleFlow(viewModel.photoOpened) { photo ->
            when (photo) {
                is EventPhoto.Local -> binding.photoImageView.setImageBitmap(photo.bitmap)
                is EventPhoto.Remote -> {
                    Glide.with(requireContext())
                        .load(photo.photoUrl)
                        .into(binding.photoImageView)
                }
                null -> Unit
            }
        }

        binding.closeButton.setOnClickListener {
            navController.popBackStack()
        }
        binding.deleteButton.setOnClickListener {
            viewModel.photoOpened.value?.let { photo -> viewModel.deletePhoto(photo) }
            navController.popBackStack()
        }
    }
}
