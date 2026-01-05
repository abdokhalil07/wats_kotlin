package com.example.wtascopilot.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.wtascopilot.data.local.dao.TransactionDao
import com.example.wtascopilot.data.local.entity.RawSmsEntity
import com.example.wtascopilot.data.local.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class, RawSmsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TransactionDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}
