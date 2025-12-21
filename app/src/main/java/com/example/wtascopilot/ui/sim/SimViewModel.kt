package com.example.wtascopilot.ui.sim

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wtascopilot.util.SimUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.telephony.SubscriptionManager
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.Manifest

class SimViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SimUiState())
    val uiState: StateFlow<SimUiState> = _uiState

    fun loadSims(context: Context) {
        // التحقق من الإذن مرة أخرى داخل الدالة للأمان
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
        ) {
            // إذا لم يكن هناك إذن، لا تفعل شيئاً (القائمة ستظل فارغة)
            return
        }

        try {
            val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
            val activeSims = subscriptionManager.activeSubscriptionInfoList ?: emptyList()

            val sims = activeSims.map { subInfo ->
                // محاولة جلب الرقم
                val rawNumber = subInfo.number
                SimItem(
                    carrierName = subInfo.displayName?.toString() ?: subInfo.carrierName?.toString() ?: "Unknown",
                    // نعالج مشكلة الرقم الفارغ هنا
                    number = if (rawNumber.isNullOrEmpty()) "" else rawNumber
                )
            }

            _uiState.value = _uiState.value.copy(sims = sims)

        } catch (e: Exception) {
            e.printStackTrace()
            // في حال حدوث خطأ، القائمة تظل فارغة ولا ينهار التطبيق
        }
    }

    fun selectSim(number: String) {
        _uiState.value = _uiState.value.copy(selectedSim = number)
    }
}
