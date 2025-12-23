package com.example.wtascopilot.data.repository

import android.content.Context
import android.util.Log
import com.example.wtascopilot.data.local.UserStorage
import com.example.wtascopilot.data.remote.RetrofitClient
import com.example.wtascopilot.data.remote.model.LoginRequest

class LoginRepository {

    // أضفنا Context كـ Parameter للدالة
    suspend fun login(context: Context, username: String, password: String): Boolean {
        return try {
            val request = LoginRequest(username, password)
            val response = RetrofitClient.api.login(request)

            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!

                if (loginResponse.accountId != null) {
                    Log.d("LoginRepo", "Login Success, ID: ${loginResponse.accountId}")

                    // --- التعديل هنا: حفظ البيانات ---
                    UserStorage.saveUser(context, loginResponse)
                    // ---------------------------------

                    true
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}