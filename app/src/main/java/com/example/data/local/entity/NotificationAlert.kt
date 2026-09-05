package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_alerts")
data class NotificationAlert(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val bookingId: Long,
    val tokenNumber: String,
    val title: String,
    val message: String,
    val smsText: String,
    val category: String, // "SLOT", "ARRIVAL", "QUEUE", "TESTING", "WEIGHMENT", "PAYMENT"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
