package com.andrew.tasky.presentation.edit

import androidx.lifecycle.ViewModel
import com.andrew.tasky.util.EditType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditViewModel (): ViewModel() {

    private val _editType = MutableStateFlow(EditType.DESCRIPTION)
    val editType = _editType.asStateFlow()

    fun setEditType(x: EditType) {
        _editType.value = x
    }

}