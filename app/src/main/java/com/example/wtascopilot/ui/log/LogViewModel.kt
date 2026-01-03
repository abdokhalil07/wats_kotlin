package com.example.wtascopilot.ui.log

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wtascopilot.data.modle.Transaction
import com.example.wtascopilot.data.repository.TransactionRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LogViewModel(context: Context) : ViewModel() {

    // 1. تعريف الـ Repository للوصول للبيانات المحلية
    private val repository = TransactionRepositoryImpl(context)

    // 2. تعريف الـ State اللي الشاشة هتعرضه (قائمة العمليات)
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    // 3. حالة التحميل (عشان لو عايز تعرض ProgressBar)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // تحميل البيانات أول ما الـ ViewModel يشتغل
        loadLogs()
    }

    fun loadLogs() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // جلب كل العمليات من الـ Repository
                // ملاحظة: تأكد أن Repository يرجع Flow لضمان التحديث اللحظي
                repository.getAllLocalTransactions().collect { list ->
                    _transactions.value = list
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // دالة لمسح السجل لو حبيت (اختياري)
    fun clearLogs() {
        viewModelScope.launch {
            // repository.deleteAllTransactions() // لو ضفت الدالة دي في الـ Dao
            loadLogs()
        }
    }

    fun toggleSync(hash: String) {
        viewModelScope.launch {
            repository.toggleSyncStatus(hash)
            // مش محتاج تنادي refresh لأن الـ Flow هيحس بالتغيير لوحده
        }
    }
}