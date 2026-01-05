package com.example.wtascopilot.ui.Inspector

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wtascopilot.data.modle.SmsTransaction
import com.example.wtascopilot.data.modle.Transaction
import com.example.wtascopilot.data.repository.TransactionRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class SmsLogModel(context: Context) : ViewModel() {

    private val repository = TransactionRepositoryImpl(context)

    // 2. تعريف الـ State اللي الشاشة هتعرضه (قائمة العمليات)
    private val _transactions = MutableStateFlow<List<SmsTransaction>>(emptyList())
    val smstransactions: StateFlow<List<SmsTransaction>> = _transactions.asStateFlow()
    // 3. حالة التحميل (عشان لو عايز تعرض ProgressBar)
    private val _isLoading = MutableStateFlow(false)

    init {

        loadLogs()
    }

    fun loadLogs() {
        viewModelScope.launch {
            try {

                repository.getAllLocalSms().collect { list ->
                    _transactions.value = list
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
