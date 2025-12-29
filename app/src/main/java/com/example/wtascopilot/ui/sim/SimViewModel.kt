package com.example.wtascopilot.ui.sim


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wtascopilot.data.local.SimStorage
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

            val simInfo = simUiModel.simInfo

            val success = if (simUiModel.isRegistered) {
                val res = repository.stopSim(simInfo.phoneNumber)
                if (res) SimStorage.clearSim(context) // مسح البيانات لو عمل Stop
                res
            } else {
                val res = repository.registerSim(
                    accountId = UserStorage.getAccountId(context),
                    phoneNumber = simInfo.phoneNumber,
                    phoneName = simInfo.phoneName,
                    carrier = simInfo.carrierName,
                    slot = simInfo.slotIndex
                )
                if (res) {
                    // حفظ البيانات محلياً فور النجاح
                    SimStorage.saveActiveSim(
                        context,
                        simInfo.phoneNumber,
                        simInfo.slotIndex,
                        simInfo.subscriptionId
                    )
                }
                res
            }

            if (success) {
                // لو نجحنا، نعيد تحميل القائمة لتحديث الحالة
                loadSimCards(context)
            }
        }
    }
}