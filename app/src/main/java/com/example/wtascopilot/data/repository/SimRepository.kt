package com.example.wtascopilot.data.repository

import com.example.wtascopilot.data.remote.RetrofitClient
import com.example.wtascopilot.data.remote.model.SimRequest

class SimRepository {

    // دالة عامة تستخدم للتحقق، الإضافة، والحذف حسب نوع الطلب
    suspend fun checkSimStatus(phoneNumber: String): Boolean {
        return try {
            val response = RetrofitClient.api.checkSimStatus(SimRequest(phoneNumber))
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.isRegistered
            } else false
        } catch (e: Exception) {
            false
        } as Boolean
    }

    suspend fun registerSim(accountId: Int, phoneNumber: String, carrier: String, slot: Int): Boolean {
        return try {
            val request = SimRequest(phoneNumber)
            val response = RetrofitClient.api.addSim(request)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun stopSim(phoneNumber: String): Boolean {
        return try {
            val request = SimRequest(phoneNumber)
            val response = RetrofitClient.api.removeSim(request)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}