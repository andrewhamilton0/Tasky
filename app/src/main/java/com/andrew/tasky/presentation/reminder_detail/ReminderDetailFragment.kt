package com.andrew.tasky.presentation.reminder_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.andrew.tasky.R

class ReminderDetailFragment : Fragment() {

    companion object {
        fun newInstance() = ReminderDetailFragment()
    }

    private lateinit var viewModel: ReminderDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reminder_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ReminderDetailViewModel::class.java)
        // TODO: Use the ViewModel
    }
}