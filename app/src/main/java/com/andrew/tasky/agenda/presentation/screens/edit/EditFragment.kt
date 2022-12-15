package com.andrew.tasky.agenda.presentation.screens.edit

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.andrew.tasky.R
import com.andrew.tasky.agenda.util.EditType
import com.andrew.tasky.databinding.FragmentEditBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EditFragment : Fragment(R.layout.fragment_edit) {

    private lateinit var viewModel: EditViewModel
    private lateinit var binding: FragmentEditBinding
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditBinding.bind(view)
        navController = Navigation.findNavController(view)
        viewModel = ViewModelProvider(this).get(EditViewModel::class.java)

        subscribeToObservables()
        setupEditType()

        binding.backButton.setOnClickListener {
            navController.popBackStack()
        }

        binding.saveButton.setOnClickListener {
            val input = binding.inputEditText.text.toString()
            val inputBundle = Bundle()
            inputBundle.putString("INPUT", input)

            setFragmentResult("INPUT_REQUEST_KEY", inputBundle)
            navController.popBackStack()
        }
    }

    private fun subscribeToObservables() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editType.collectLatest {
                    when (it) {
                        EditType.DESCRIPTION -> {
                            binding.editTypeTitle.text = getString(R.string.edit_description_text)
                            binding.inputEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16F)
                        }
                        EditType.TITLE -> {
                            binding.editTypeTitle.text = getString(R.string.edit_title_text)
                            binding.inputEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24F)
                        }
                    }
                }
            }
        }
    }

    private fun setupEditType() {
        setFragmentResultListener("EDIT_TYPE_AND_TEXT_REQUEST_KEY") { resultKey, bundle ->
            if (resultKey == "EDIT_TYPE_AND_TEXT_REQUEST_KEY") {
                bundle.getString("EDIT_TYPE")?.let {
                    viewModel.setEditType(EditType.valueOf(it))
                }
                binding.inputEditText.setText(bundle.getString("TEXT"))
            }
        }
    }
}
