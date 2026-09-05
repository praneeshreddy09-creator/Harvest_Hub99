package com.example.data.repository

import com.example.data.local.dao.HarvestHubDao
import com.example.data.local.entity.FarmerProfile
import com.example.data.local.entity.NotificationAlert
import com.example.data.local.entity.ProcurementCenter
import com.example.data.local.entity.SlotBooking
import kotlinx.coroutines.flow.Flow
import kotlin.random.Random

class HarvestHubRepository(private val dao: HarvestHubDao) {

    val farmerProfile: Flow<FarmerProfile?> = dao.getFarmerProfile()
    val centers: Flow<List<ProcurementCenter>> = dao.getAllCenters()
    val allBookings: Flow<List<SlotBooking>> = dao.getAllBookings()
    val latestBooking: Flow<SlotBooking?> = dao.getLatestBooking()
    val notifications: Flow<List<NotificationAlert>> = dao.getAllNotifications()
    val unreadCount: Flow<Int> = dao.getUnreadNotificationCount()

    fun getBookingById(id: Long): Flow<SlotBooking?> = dao.getBookingById(id)

    suspend fun updateProfile(profile: FarmerProfile) {
        dao.insertFarmerProfile(profile)
    }

    suspend fun markAllNotificationsRead() {
        dao.markAllNotificationsRead()
    }

    suspend fun bookSlot(
        centerId: Long,
        centerName: String,
        cropVariety: String,
        estimatedYield: Double,
        vehicleType: String,
        vehicleNumber: String,
        date: String,
        timeSlot: String
    ): Long {
        val randomTokenSuffix = Random.nextInt(100, 999)
        val tokenNum = "HH-$randomTokenSuffix"
        val gate = if (vehicleType.contains("Tractor", ignoreCase = true)) {
            "Gate 2 (Tractor Entry)"
        } else if (vehicleType.contains("Truck", ignoreCase = true)) {
            "Gate 1 (Heavy Truck Entry)"
        } else {
            "Gate 3 (Small Vehicle Bay)"
        }

        // Calculate arrival recommendation (45 mins prior to slot window)
        val departure = when {
            timeSlot.startsWith("08:00") -> "07:15 AM"
            timeSlot.startsWith("10:00") -> "09:15 AM"
            timeSlot.startsWith("12:00") -> "11:15 AM"
            timeSlot.startsWith("02:00") -> "01:15 PM"
            else -> "03:15 PM"
        }

        val rate = if (cropVariety.contains("Grade A", ignoreCase = true)) 2320.0 else 2300.0
        val gross = (estimatedYield * 100) + 2300.0
        val tare = 2300.0
        val total = estimatedYield * rate

        val booking = SlotBooking(
            tokenNumber = tokenNum,
            centerId = centerId,
            centerName = centerName,
            cropVariety = cropVariety,
            estimatedYieldQuintals = estimatedYield,
            vehicleType = vehicleType,
            vehicleNumber = vehicleNumber.ifBlank { "OD-17-TR-4581" },
            bookedDate = date,
            slotTimeWindow = timeSlot,
            recommendedDepartureTime = departure,
            assignedGate = gate,
            status = "CONFIRMED",
            tokensAheadInQueue = 5,
            estimatedWaitMinutes = 40,
            moisturePercent = 14.1,
            impuritiesPercent = 1.0,
            qualityGrade = if (cropVariety.contains("Grade A")) "Grade A (FAQ Standard)" else "Common (FAQ Standard)",
            mspRatePerQuintal = rate,
            grossWeightKg = gross,
            tareWeightKg = tare,
            netWeightQuintals = estimatedYield,
            totalAmount = total,
            weighmentSlipNumber = "WS-2026-${Random.nextInt(10000, 99999)}",
            dbtStatus = "PENDING"
        )

        val id = dao.insertBooking(booking)

        // Generate immediate SMS Alert for slot confirmation
        dao.insertNotification(
            NotificationAlert(
                bookingId = id,
                tokenNumber = tokenNum,
                title = "Delivery Slot Confirmed - Token $tokenNum",
                message = "Your Paddy booking for $estimatedYield Quintals is confirmed at $centerName for $timeSlot.",
                smsText = "[GOV-PADDY] Dear Farmer, Token #$tokenNum assigned for $cropVariety. Report to $gate on $date ($timeSlot). Recommended departure: $departure. Entry strictly by Token SMS.",
                category = "SLOT",
                timestamp = System.currentTimeMillis()
            )
        )

        // Queue departure advisory SMS
        dao.insertNotification(
            NotificationAlert(
                bookingId = id,
                tokenNumber = tokenNum,
                title = "Timely Arrival Advisory",
                message = "Depart at $departure to ensure 0 waiting time at $gate.",
                smsText = "[GOV-PADDY] Advisory for Token #$tokenNum: Estimated 5 vehicles ahead. Recommended departure from farm: $departure. Live queue tracking is active.",
                category = "ARRIVAL",
                timestamp = System.currentTimeMillis() + 1000
            )
        )

        return id
    }

    suspend fun advanceBookingLifecycle(booking: SlotBooking): SlotBooking {
        val updated = when (booking.status) {
            "CONFIRMED" -> {
                // Advance to ARRIVED at gate
                val b = booking.copy(
                    status = "ARRIVED",
                    tokensAheadInQueue = 2,
                    estimatedWaitMinutes = 15
                )
                dao.insertNotification(
                    NotificationAlert(
                        bookingId = b.id,
                        tokenNumber = b.tokenNumber,
                        title = "Gate Entry Verified - Proceed to Assaying",
                        message = "Your vehicle ${b.vehicleNumber} has entered ${b.assignedGate}. Proceed to Quality Testing Bay 2.",
                        smsText = "[GOV-PADDY] Token #${b.tokenNumber} verified at ${b.assignedGate}. Security checked. Proceed to Digital Quality Assaying Bay 2.",
                        category = "GATE_ENTRY",
                        timestamp = System.currentTimeMillis()
                    )
                )
                b
            }
            "ARRIVED" -> {
                // Advance to TESTED (Quality Assayed)
                val moisture = 14.3
                val impurities = 1.1
                val b = booking.copy(
                    status = "TESTED",
                    tokensAheadInQueue = 1,
                    estimatedWaitMinutes = 8,
                    moisturePercent = moisture,
                    impuritiesPercent = impurities,
                    qualityGrade = "Grade A (Moisture ${moisture}%, Impurity ${impurities}%)"
                )
                dao.insertNotification(
                    NotificationAlert(
                        bookingId = b.id,
                        tokenNumber = b.tokenNumber,
                        title = "Digital Quality Approved: Grade A",
                        message = "Moisture tested at ${moisture}% (Standard < 17%). Instant Quality Slip generated. Proceed to Digital Weighbridge.",
                        smsText = "[GOV-PADDY] Token #${b.tokenNumber} Quality Result: Moisture ${moisture}%, Impurities ${impurities}%. Grade A APPROVED at MSP Rs ${b.mspRatePerQuintal.toInt()}/Qtl. No price cut applied.",
                        category = "TESTING",
                        timestamp = System.currentTimeMillis()
                    )
                )
                b
            }
            "TESTED" -> {
                // Advance to WEIGHED (Digital Weighbridge Scale slip)
                val netWeight = booking.estimatedYieldQuintals
                val totalPayable = netWeight * booking.mspRatePerQuintal
                val b = booking.copy(
                    status = "WEIGHED",
                    tokensAheadInQueue = 0,
                    estimatedWaitMinutes = 0,
                    grossWeightKg = (netWeight * 100) + 2350.0,
                    tareWeightKg = 2350.0,
                    netWeightQuintals = netWeight,
                    totalAmount = totalPayable,
                    dbtStatus = "SANCTIONED"
                )
                dao.insertNotification(
                    NotificationAlert(
                        bookingId = b.id,
                        tokenNumber = b.tokenNumber,
                        title = "Digital Weighment Slip Recorded",
                        message = "Tamper-proof scale logged ${netWeight} Quintals. Total Payable: ₹${totalPayable.toInt()}.",
                        smsText = "[GOV-PADDY] Token #${b.tokenNumber} Weighed at Weighbridge 1. Net Paddy: ${netWeight} Qtl. Total: Rs ${totalPayable.toInt()}. Weighment Slip #${b.weighmentSlipNumber} linked to Aadhaar DBT.",
                        category = "WEIGHMENT",
                        timestamp = System.currentTimeMillis()
                    )
                )
                b
            }
            "WEIGHED" -> {
                // Advance to COMPLETED (Direct Benefit Transfer DBT credited)
                val utr = "UTR-98273" + Random.nextInt(100000, 999999)
                val b = booking.copy(
                    status = "COMPLETED",
                    dbtStatus = "DISBURSED",
                    dbtTransactionId = utr
                )
                dao.insertNotification(
                    NotificationAlert(
                        bookingId = b.id,
                        tokenNumber = b.tokenNumber,
                        title = "DBT Payment Credited: ₹${b.totalAmount.toInt()}",
                        message = "Direct Benefit Transfer of ₹${b.totalAmount.toInt()} transferred to ${b.dbtBankRef} via $utr.",
                        smsText = "[GOV-PADDY] Payment Alert: Rs ${b.totalAmount.toInt()} directly transferred to your Aadhaar-linked ${b.dbtBankRef} on ${System.currentTimeMillis()}. Ref: $utr. No middleman deductions.",
                        category = "PAYMENT",
                        timestamp = System.currentTimeMillis()
                    )
                )
                b
            }
            else -> {
                // Already completed, can reset or return
                booking
            }
        }
        dao.updateBooking(updated)
        return updated
    }

    suspend fun decrementQueue(booking: SlotBooking): SlotBooking {
        val newAhead = if (booking.tokensAheadInQueue > 0) booking.tokensAheadInQueue - 1 else 0
        val newWait = newAhead * 8
        val updated = booking.copy(
            tokensAheadInQueue = newAhead,
            estimatedWaitMinutes = newWait
        )
        dao.updateBooking(updated)

        // If down to 1 or 2 tokens, send proactive queue movement SMS
        if (newAhead == 1) {
            dao.insertNotification(
                NotificationAlert(
                    bookingId = booking.id,
                    tokenNumber = booking.tokenNumber,
                    title = "Queue Movement Alert: You are Next!",
                    message = "Only 1 vehicle ahead of you at ${booking.assignedGate}. Please start engine and move to the barrier.",
                    smsText = "[GOV-PADDY] Alert for Token #${booking.tokenNumber}: Vehicle ahead moving inside. Please approach ${booking.assignedGate} now.",
                    category = "QUEUE",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
        return updated
    }
}
