package com.andrew.tasky.agenda.presentation.screens.photo_detail

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.andrew.tasky.R
import com.andrew.tasky.databinding.FragmentPhotoDetailBinding

class PhotoDetailFragment : Fragment(R.layout.fragment_photo_detail) {

    private lateinit var binding: FragmentPhotoDetailBinding
    private val viewModel: PhotoDetailViewModel by viewModels()
    private lateinit var navController: NavController
    private val args: PhotoDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPhotoDetailBinding.bind(view)
        navController = Navigation.findNavController(view)

        binding.photoImageView.setImageURI(Uri.parse(args.photoUriString))
        binding.closeButton.setOnClickListener {
            navController.popBackStack()
        }
        binding.deleteButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("DELETE_PHOTO_INDEX", args.photoIndex)
            setFragmentResult("REQUEST_KEY", bundle)
            navController.popBackStack()
        }
    }
}
