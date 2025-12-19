package com.example.wtascopilot.ui.sim

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wtascopilot.util.SimUtils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SimViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SimUiState())
    val uiState: StateFlow<SimUiState> = _uiState

    fun loadSims(context: Context) {
        val sims = SimUtils.getSimCards(context).map {
            SimItem(
                carrierName = it.carrierName.toString(),
                number = SimUtils.getSimNumber(it)
            )
        }

        _uiState.value = _uiState.value.copy(sims = sims)
    }

    fun selectSim(number: String) {
        _uiState.value = _uiState.value.copy(selectedSim = number)
    }
}
