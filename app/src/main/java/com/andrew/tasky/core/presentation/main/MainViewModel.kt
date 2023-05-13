package com.andrew.tasky.core.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrew.tasky.agenda.domain.AgendaRepository
import com.andrew.tasky.auth.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val agendaRepository: AgendaRepository
) : ViewModel() {

    private val _isUserLoggedIn = Channel<Boolean>()
    val isUserLoggedIn = _isUserLoggedIn.receiveAsFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private suspend fun deleteAllPersistedNotifications() {
        agendaRepository.deleteAllPersistedNotifs()
    }

    init {
        viewModelScope.launch {
            _isUserLoggedIn.send(authRepository.isAuthorizedToLogin())
            _isLoading.value = false
        }
        viewModelScope.launch {
            deleteAllPersistedNotifications()
        }
    }
}
