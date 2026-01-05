package com.example.wtascopilot.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "raw_sms")
data class RawSmsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String?,
    val body: String?,
    val timestamp: Long? = System.currentTimeMillis()
)