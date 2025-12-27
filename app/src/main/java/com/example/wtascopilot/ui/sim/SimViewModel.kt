package com.example.wtascopilot.ui.sim


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wtascopilot.data.local.UserStorage
import com.example.wtascopilot.data.repository.SimRepository
import com.example.wtascopilot.util.SimUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SimViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SimUiState())
    val uiState: StateFlow<SimUiState> = _uiState.asStateFlow()

    private val repository = SimRepository()

    // تحميل الشرائح ودمجها مع حالة السيرفر
    fun loadSimCards(context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // 1. جلب الشرائح من الموبايل
            val localSims = SimUtils.getSimCards(context)
            val accountId = UserStorage.getAccountId(context)

            // 2. تحويلها لـ SimUiModel والتحقق من السيرفر لكل شريحة
            val simsWithStatus = localSims.map { sim ->
                // نسأل السيرفر: هل الرقم ده متسجل؟
                val isRegistered = repository.checkSimStatus(sim.phoneNumber)
                SimUiModel(simInfo = sim, isRegistered = isRegistered)
            }

            _uiState.value = SimUiState(simCards = simsWithStatus, isLoading = false)
        }
    }

    // دالة الزر: لو مسجلة يعمل Stop، لو مش مسجلة يعمل Register
    fun toggleSimRegistration(context: Context, simUiModel: SimUiModel) {
        viewModelScope.launch {
            val accountId = UserStorage.getAccountId(context)
            val phoneNumber = simUiModel.simInfo.phoneNumber
            val phoneName = simUiModel.simInfo.phoneName
            val carrier = simUiModel.simInfo.carrierName
            val slot = simUiModel.simInfo.slotIndex

            // تغيير حالة التحميل لهذا العنصر فقط (اختياري لتحسين الـ UX)
            // ...

            val success = if (simUiModel.isRegistered) {
                // لو مسجلة -> الغاء تسجيل (Stop)
                repository.stopSim(phoneNumber)
            } else {
                // لو مش مسجلة -> تسجيل (Activate)
                repository.registerSim(accountId, phoneNumber, phoneName, carrier, slot)
            }

            if (success) {
                // لو نجحنا، نعيد تحميل القائمة لتحديث الحالة
                loadSimCards(context)
            }
        }
    }
}