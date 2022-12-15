package com.andrew.tasky.agenda.presentation.screens.edit

import androidx.lifecycle.ViewModel
import com.andrew.tasky.agenda.util.EditType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditViewModel() : ViewModel() {

    private val _editType = MutableStateFlow(EditType.DESCRIPTION)
    val editType = _editType.asStateFlow()
    fun setEditType(editType: EditType) {
        _editType.value = editType
    }
}
