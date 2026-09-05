package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "slot_bookings")
data class SlotBooking(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val tokenNumber: String, // e.g. "HH-108"
    val farmerId: Long = 1L,
    val centerId: Long,
    val centerName: String,
    val cropVariety: String, // "Paddy (Grade A)", "Paddy (Common)", "Basmati 1121"
    val estimatedYieldQuintals: Double,
    val vehicleType: String, // "Tractor Trolley", "Mini Truck", "Pickup Truck"
    val vehicleNumber: String,
    val bookedDate: String, // "Today, 05 Sep 2026"
    val slotTimeWindow: String, // "10:00 AM - 12:00 PM"
    val recommendedDepartureTime: String, // "09:15 AM"
    val assignedGate: String, // "Gate 2 (Tractor Entry)"
    val status: String, // "CONFIRMED", "ARRIVED", "TESTED", "WEIGHED", "COMPLETED"
    val tokensAheadInQueue: Int, // e.g. 4
    val estimatedWaitMinutes: Int, // e.g. 35

    // On-site Instant Quality Testing Results
    val moisturePercent: Double = 14.2, // standard benchmark < 17%
    val impuritiesPercent: Double = 1.1, // standard benchmark < 2%
    val qualityGrade: String = "Grade A (FAQ Standard)",
    val mspRatePerQuintal: Double = 2320.0, // Official MSP ₹2,320/qtl

    // Digital Weighbridge Records
    val grossWeightKg: Double = 6840.0,
    val tareWeightKg: Double = 2340.0,
    val netWeightQuintals: Double = 45.0, // (6840 - 2340) / 100 = 45.0 quintals
    val totalAmount: Double = 104400.0, // 45.0 * 2320 = ₹104,400
    val weighmentSlipNumber: String = "WS-2026-99120",

    // Direct Benefit Transfer (DBT) Status
    val dbtStatus: String = "INITIATED", // "PENDING", "INITIATED", "DISBURSED"
    val dbtTransactionId: String = "UTR-982734190123",
    val dbtBankRef: String = "SBI A/C •••• 4821",
    val createdAtTimestamp: Long = System.currentTimeMillis()
)
