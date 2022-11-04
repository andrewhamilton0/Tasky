package com.andrew.tasky.presentation.views

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.andrew.tasky.R
import com.andrew.tasky.databinding.CvReminderLayoutBinding

class ReminderLayoutCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding = CvReminderLayoutBinding.bind(
        inflate(context, R.layout.cv_reminder_layout, this)
    )
}
