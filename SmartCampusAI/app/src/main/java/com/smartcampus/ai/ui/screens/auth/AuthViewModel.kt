package com.smartcampus.ai.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartcampus.ai.data.repository.AuthRepository
import com.smartcampus.ai.domain.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─────────────────────────────────────────────
//  AUTH VIEW MODEL
// ─────────────────────────────────────────────
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<Unit>?>(null)
    val loginState: StateFlow<UiState<Unit>?> = _loginState.asStateFlow()

    private val _signupState = MutableStateFlow<UiState<Unit>?>(null)
    val signupState: StateFlow<UiState<Unit>?> = _signupState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = UiState.Error("Please fill in all fields")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _loginState.value = UiState.Error("Please enter a valid email")
            return
        }
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            val result = authRepository.login(email, password)
            _loginState.value = result.fold(
                onSuccess = { UiState.Success(Unit) },
                onFailure = { UiState.Error(it.message ?: "Login failed") }
            )
        }
    }

    fun signup(name: String, email: String, password: String, confirmPassword: String) {
        when {
            name.isBlank() || email.isBlank() || password.isBlank() ->
                _signupState.value = UiState.Error("Please fill in all fields")
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                _signupState.value = UiState.Error("Please enter a valid email")
            password.length < 6 ->
                _signupState.value = UiState.Error("Password must be at least 6 characters")
            password != confirmPassword ->
                _signupState.value = UiState.Error("Passwords do not match")
            else -> viewModelScope.launch {
                _signupState.value = UiState.Loading
                val result = authRepository.signup(name, email, password)
                _signupState.value = result.fold(
                    onSuccess = { UiState.Success(Unit) },
                    onFailure = { UiState.Error(it.message ?: "Signup failed") }
                )
            }
        }
    }

    fun clearStates() {
        _loginState.value = null
        _signupState.value = null
    }
}
