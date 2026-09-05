package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "procurement_centers")
data class ProcurementCenter(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val centerCode: String,
    val district: String,
    val address: String,
    val dailyCapacityQuintals: Double,
    val currentSlotOccupancyPercent: Int,
    val activeGates: String,
    val currentServingToken: String,
    val totalInQueue: Int,
    val avgProcessingMinutesPerVehicle: Int,
    val contactHelpline: String
)
