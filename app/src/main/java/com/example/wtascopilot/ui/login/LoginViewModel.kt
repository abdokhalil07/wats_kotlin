package com.example.wtascopilot.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wtascopilot.data.repository.LoginRepository
import com.example.wtascopilot.data.repository.TransactionRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repo: LoginRepository


) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onUsernameChanged(value: String) {
        _uiState.value = _uiState.value.copy(username = value)
    }

    fun onPasswordChanged(value: String) {
        _uiState.value = _uiState.value.copy(password = value)
    }

    fun login(context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value

            // تمرير context للـ repo
            val result = repo.login(context, state.username, state.password)

            if (result) {
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(error = "بيانات الدخول غير صحيحة")
            }
        }
    }
}
