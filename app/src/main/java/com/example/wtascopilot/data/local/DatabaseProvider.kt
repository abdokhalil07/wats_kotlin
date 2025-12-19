package com.example.wtascopilot.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var INSTANCE: TransactionDatabase? = null

    fun getDatabase(context: Context): TransactionDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                TransactionDatabase::class.java,
                "transactions_db"
            ).build()

            INSTANCE = instance
            instance
        }
    }
}
