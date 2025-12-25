package com.example.wtascopilot.ui.sim

import com.example.wtascopilot.util.SimUtils


data class SimUiModel(
    val simInfo: SimUtils.SimInfo,
    val isRegistered: Boolean = false, // هل هي مسجلة في الـ API؟
    val isLoading: Boolean = false // عشان لو بنحمل نظهر Loading spinner
)

data class SimUiState(
    val simCards: List<SimUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)