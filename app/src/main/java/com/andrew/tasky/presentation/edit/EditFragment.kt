package com.andrew.tasky.presentation.edit

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.andrew.tasky.R
import com.andrew.tasky.databinding.FragmentEditBinding
import com.andrew.tasky.util.EditType

class EditFragment : Fragment(R.layout.fragment_edit) {

    private lateinit var viewModel: EditViewModel
    private lateinit var binding: FragmentEditBinding
    private lateinit var navController: NavController
    private lateinit var editType: EditType

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditBinding.bind(view)
        navController = Navigation.findNavController(view)
        viewModel = ViewModelProvider(this).get(EditViewModel::class.java)
        editType = EditType.values()[arguments?.getInt("editType")!!]

        setupEditType()

        binding.backButton.setOnClickListener {
            navController.popBackStack()
        }

        binding.saveButton.setOnClickListener {
            navController.popBackStack()
        }
    }

    private fun setupEditType(){
        when(editType){
            EditType.DESCRIPTION -> {
                binding.editTypeTitle.text = getString(R.string.edit_description_text)
            }
            EditType.TITLE -> {
                binding.editTypeTitle.text = getString(R.string.edit_title_text)
            }
        }
    }
}