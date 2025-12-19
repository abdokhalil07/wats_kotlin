package com.example.wtascopilot.data.local

import android.content.Context

object SimStorage {

    private const val PREF = "sim_pref"
    private const val KEY_SIM = "selected_sim"

    fun saveSim(context: Context, number: String) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_SIM, number)
            .apply()
    }

    fun getSim(context: Context): String? {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(KEY_SIM, null)
    }
}
