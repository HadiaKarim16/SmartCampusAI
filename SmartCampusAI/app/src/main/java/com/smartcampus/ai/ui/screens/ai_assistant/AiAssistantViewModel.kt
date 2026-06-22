package com.smartcampus.ai.ui.screens.ai_assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcampus.ai.data.repository.AiRepository
import com.smartcampus.ai.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiAssistantViewModel @Inject constructor(
    private val repository: AiRepository
) : ViewModel() {

    val chatHistory: StateFlow<List<AiChatMessage>> = repository.getChatHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedSubject = MutableStateFlow("General")
    val selectedSubject: StateFlow<String> = _selectedSubject.asStateFlow()

    fun sendMessage(message: String) {
        if (message.isBlank() || _isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = repository.sendMessage(message.trim(), _selectedSubject.value)
            result.onFailure { _errorMessage.value = it.message ?: "Failed to get response" }
            _isLoading.value = false
        }
    }

    fun setSubject(subject: String) { _selectedSubject.value = subject }
    fun clearHistory() { viewModelScope.launch { repository.clearHistory() } }
    fun clearError() { _errorMessage.value = null }
}
