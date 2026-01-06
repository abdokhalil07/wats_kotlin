package com.example.wtascopilot.data.local

import android.content.Context

object SimStorage {
    private const val PREF = "sim_pref"

    private fun getPhoneKey(subId: Int) = "phone_number_$subId"
    private const val KEY_PHONE_NUMBER = "selected_sim_number"
    private const val KEY_SLOT_INDEX = "selected_sim_slot"
    private const val KEY_SUB_ID = "selected_sim_sub_id"
    private const val KEY_IS_ACTIVE = "is_sim_active"

    fun saveActiveSim(context: Context, number: String, slot: Int, subId: Int) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putString("number_$subId", number) // مفتاح فريد لكل شريحة
            .putInt("slot_$subId", slot)
            .putBoolean("is_active_$subId", true)
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

    fun getPhoneNumberForSub(context: Context, incomingSubId: Int?): String? {
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        return prefs.getString("number_$incomingSubId", null)
    }

    fun getActiveSimPhoneForSub(context: Context, subId: Int): String? {
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        if (prefs.getInt(KEY_SUB_ID, -1) != subId) return null
        return prefs.getString(KEY_PHONE_NUMBER, null)
    }



}
