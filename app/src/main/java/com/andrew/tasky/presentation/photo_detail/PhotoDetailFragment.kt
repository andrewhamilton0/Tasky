package com.andrew.tasky.presentation.photo_detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andrew.tasky.R

class PhotoDetailFragment : Fragment() {

    companion object {
        fun newInstance() = PhotoDetailFragment()
    }

    private lateinit var viewModel: PhotoDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_photo_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PhotoDetailViewModel::class.java)
        // TODO: Use the ViewModel
    }

}