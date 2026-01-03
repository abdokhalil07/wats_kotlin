package com.example.wtascopilot.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.wtascopilot.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE isSynced = 0")
    suspend fun getUnSynced(): List<TransactionEntity>

    // نستخدم الـ messageHash لتحديث حالة المزامنة لأنه الـ PrimaryKey
    @Query("UPDATE transactions SET isSynced = 1 WHERE messageHash = :messageHash")
    suspend fun markAsSynced(messageHash: String)

    // الحفاظ على اسم الدالة checkIfExists وتعديل منطقها ليفحص الـ hash
    @Query("SELECT COUNT(*) FROM transactions WHERE messageHash = :hash")
    suspend fun checkIfExists(hash: String): Int

    @Query("SELECT * FROM transactions ORDER BY dateTime DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("UPDATE transactions SET isSynced = CASE WHEN isSynced = 1 THEN 0 ELSE 1 END WHERE messageHash = :hash")
    suspend fun toggleSyncStatus(hash: String)

}
