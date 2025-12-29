package com.example.wtascopilot.data.local

import android.content.Context

object SimStorage {
    private const val PREF = "sim_pref"
    private const val KEY_PHONE_NUMBER = "selected_sim_number"
    private const val KEY_SLOT_INDEX = "selected_sim_slot"
    private const val KEY_SUB_ID = "selected_sim_sub_id"
    private const val KEY_IS_ACTIVE = "is_sim_active"

    fun saveActiveSim(context: Context, number: String, slot: Int, subId: Int) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_PHONE_NUMBER, number)
            .putInt(KEY_SLOT_INDEX, slot)
            .putInt(KEY_SUB_ID, subId)
            .putBoolean(KEY_IS_ACTIVE, true)
            .apply()
    }

    fun getSavedSubId(context: Context): Int {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getInt(KEY_SUB_ID, -1)
    }

    fun getSavedPhoneNumber(context: Context): String? {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY_PHONE_NUMBER, null)
    }

    fun clearSim(context: Context) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().clear().apply()
    }
}
