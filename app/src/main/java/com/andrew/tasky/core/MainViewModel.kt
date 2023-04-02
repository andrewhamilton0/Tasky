package com.andrew.tasky.core

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrew.tasky.core.data.PrefsKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val prefs: SharedPreferences
) : ViewModel() {

    private val userIsInitiallyLoggedInChannel = Channel<Unit>()
    val userIsInitiallyLoggedIn = userIsInitiallyLoggedInChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            if (prefs.contains(PrefsKeys.JWT)) {
                userIsInitiallyLoggedInChannel.send(Unit)
            }
        }
    }
}
