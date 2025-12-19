package com.example.wtascopilot.ui.sim

data class SimUiState(
    val sims: List<SimItem> = emptyList(),
    val selectedSim: String? = null
)

data class SimItem(
    val carrierName: String,
    val number: String
)
