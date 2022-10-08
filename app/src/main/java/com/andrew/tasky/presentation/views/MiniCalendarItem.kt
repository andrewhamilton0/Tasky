package com.andrew.tasky.presentation.views

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.andrew.tasky.R
import com.andrew.tasky.databinding.ItemMiniCalendarDayBinding

class MiniCalendarItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
    ): ConstraintLayout(context, attrs) {

        private val binding = ItemMiniCalendarDayBinding.bind(
            inflate(context, R.layout.item_mini_calendar_day, this)
        )
    }