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

    fun loadSimCards(context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // 1. جلب الشرائح الموجودة فعلياً في الجهاز الآن
            val localSims = SimUtils.getSimCards(context)
            val accountId = UserStorage.getAccountId(context)

            // 2. معالجة كل شريحة للمزامنة التلقائية
            val simsWithStatus = localSims.map { sim ->
                // التأكد من السيرفر هل الرقم مسجل أم لا
                val isRegisteredOnServer = repository.checkSimStatus(sim.phoneNumber)

                if (isRegisteredOnServer) {
                    // --- التعديل الجوهري هنا ---
                    // إذا كان مسجل في السيرفر، نتأكد هل له SubId محلي أم لا
                    val currentSavedSubId = SimStorage.getSavedSubId(context)

                    // إذا كان الـ SubId المحلي لا يطابق الحالي (بسبب مسح التطبيق أو تغيير الجهاز)
                    // نقوم بتحديثه فوراً ليعمل الـ SmsReceiver
                    if (currentSavedSubId != sim.subscriptionId) {
                        SimStorage.saveActiveSim(
                            context,
                            sim.phoneNumber,
                            sim.slotIndex,
                            sim.subscriptionId
                        )
                    }
                }

                SimUiModel(
                    simInfo = sim,
                    isRegistered = isRegisteredOnServer
                )
            }

            _uiState.value = SimUiState(simCards = simsWithStatus, isLoading = false)
        }
    }

    fun toggleSimRegistration(context: Context, simUiModel: SimUiModel) {
        viewModelScope.launch {
            val simInfo = simUiModel.simInfo

            val success = if (simUiModel.isRegistered) {
                val res = repository.stopSim(simInfo.phoneNumber)
                if (res) SimStorage.clearSim(context)
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
                loadSimCards(context)
            }
        }
    }
}