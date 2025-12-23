package com.example.wtascopilot.data.local

import android.content.Context
import com.example.wtascopilot.data.remote.model.LoginResponse

object UserStorage {
    private const val PREF_NAME = "user_prefs"
    private const val KEY_ACCOUNT_ID = "account_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_MAX_NUMBER = "max_number"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"

    // دالة لحفظ بيانات المستخدم كاملة
    fun saveUser(context: Context, response: LoginResponse) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            // حفظنا الـ ID كـ Int، لو كان null بنحط -1
            putInt(KEY_ACCOUNT_ID, response.accountId ?: -1)
            putString(KEY_USER_NAME, response.userName ?: "")
            putInt(KEY_MAX_NUMBER, response.maxNumber ?: 0)
            putBoolean(KEY_IS_LOGGED_IN, true) // علمنا أن المستخدم مسجل دخول
            apply()
        }
    }

    // دالة للتحقق هل المستخدم مسجل دخول أم لا
    fun isUserLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // دالة لاسترجاع الـ Account ID
    fun getAccountId(context: Context): Int {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_ACCOUNT_ID, -1)
    }

    // دالة لتسجيل الخروج (مسح البيانات)
    fun logout(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}