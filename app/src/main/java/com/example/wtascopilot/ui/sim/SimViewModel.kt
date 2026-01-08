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
import kotlinx.coroutines.withTimeout
import java.net.UnknownHostException

class SimViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SimUiState())
    val uiState: StateFlow<SimUiState> = _uiState.asStateFlow()

    private val repository = SimRepository()

    fun loadSimCards(context: Context) {
        viewModelScope.launch {
            // نفتح الـ Loading ونصفر الـ Error
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // 👈 تحديد وقت أقصى (مثلاً 10 ثواني) للعملية كلها
                withTimeout(10000L) {
                    val localSims = SimUtils.getSimCards(context)

                    val simsWithStatus = localSims.map { sim ->
                        // محاولة التأكد من السيرفر مع حماية من أخطاء الشبكة
                        val isRegisteredOnServer = try {
                            repository.checkSimStatus(sim.phoneNumber)
                        } catch (e: Exception) {
                            false // لو فشل الاتصال برقم معين نعتبره غير مسجل مؤقتاً
                        }

                        // المنطق القديم بتاع الـ SubId
                        if (isRegisteredOnServer) {
                            val currentSavedSubId = SimStorage.getSavedSubId(context)
                            if (currentSavedSubId != sim.subscriptionId) {
                                SimStorage.saveActiveSim(context, sim.phoneNumber, sim.slotIndex, sim.subscriptionId)
                            }
                        }

                        SimUiModel(simInfo = sim, isRegistered = isRegisteredOnServer)
                    }

                    _uiState.value = SimUiState(simCards = simsWithStatus, isLoading = false, error = null)
                }
            } catch (e: Exception) {
                // 👈 معالجة الخطأ لو الإنترنت فاصل أو الوقت خلص
                val errorMessage = when (e) {
                    is UnknownHostException -> "لا يوجد اتصال بالإنترنت"
                    is kotlinx.coroutines.TimeoutCancellationException -> "انتهت مهلة الاتصال بالسيرفر"
                    else -> "حدث خطأ غير متوقع: ${e.localizedMessage}"
                }
                _uiState.value = _uiState.value.copy(isLoading = false, error = errorMessage)
            }
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