package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.HarvestHubDao
import com.example.data.local.entity.FarmerProfile
import com.example.data.local.entity.NotificationAlert
import com.example.data.local.entity.ProcurementCenter
import com.example.data.local.entity.SlotBooking
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        FarmerProfile::class,
        ProcurementCenter::class,
        SlotBooking::class,
        NotificationAlert::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HarvestHubDatabase : RoomDatabase() {

    abstract fun harvestHubDao(): HarvestHubDao

    companion object {
        @Volatile
        private var INSTANCE: HarvestHubDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): HarvestHubDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HarvestHubDatabase::class.java,
                    "harvesthub_database"
                )
                .addCallback(HarvestHubDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class HarvestHubDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.harvestHubDao())
                }
            }
        }

        private suspend fun populateInitialData(dao: HarvestHubDao) {
            // Initial Farmer Profile
            val profile = FarmerProfile(
                id = 1L,
                fullName = "Rameshwar Patel",
                phone = "+91 98452 10982",
                aadhaarMasked = "XXXX-XXXX-7341",
                farmerId = "OD-PPC-2026-9812",
                village = "Attabira, Ward 4",
                district = "Bargarh",
                state = "Odisha",
                landAreaAcres = 4.5,
                bankName = "State Bank of India (Bargarh Branch)",
                bankAccountMasked = "•••• •••• •••• 4821",
                ifscCode = "SBIN0001243"
            )
            dao.insertFarmerProfile(profile)

            // Initial Procurement Mandis
            val centers = listOf(
                ProcurementCenter(
                    id = 1L,
                    name = "Bargarh Central Paddy Mandi (PPC-01)",
                    centerCode = "PPC-BGR-01",
                    district = "Bargarh",
                    address = "National Highway 53, Mandi Yard Complex",
                    dailyCapacityQuintals = 1500.0,
                    currentSlotOccupancyPercent = 65,
                    activeGates = "Gate 1 (Heavy Trucks), Gate 2 (Tractors)",
                    currentServingToken = "HH-104",
                    totalInQueue = 12,
                    avgProcessingMinutesPerVehicle = 8,
                    contactHelpline = "1800-345-6789"
                ),
                ProcurementCenter(
                    id = 2L,
                    name = "Attabira Regional Paddy Procurement Center",
                    centerCode = "PPC-ATB-02",
                    district = "Bargarh",
                    address = "Near Canal Road, Block Hub",
                    dailyCapacityQuintals = 950.0,
                    currentSlotOccupancyPercent = 45,
                    activeGates = "Gate 1 (Express Weighbridge)",
                    currentServingToken = "HH-089",
                    totalInQueue = 6,
                    avgProcessingMinutesPerVehicle = 7,
                    contactHelpline = "1800-345-6790"
                ),
                ProcurementCenter(
                    id = 3L,
                    name = "Godbhaga APMC Grain Yard",
                    centerCode = "APMC-GDB-03",
                    district = "Sambalpur",
                    address = "Grain Market Complex, Gate 3",
                    dailyCapacityQuintals = 1200.0,
                    currentSlotOccupancyPercent = 80,
                    activeGates = "Gate 1, Gate 2, Gate 3",
                    currentServingToken = "HH-215",
                    totalInQueue = 18,
                    avgProcessingMinutesPerVehicle = 10,
                    contactHelpline = "1800-345-6791"
                )
            )
            dao.insertCenters(centers)

            // Active Sample Slot Booking for Farmer
            val initialBooking = SlotBooking(
                id = 1L,
                tokenNumber = "HH-108",
                farmerId = 1L,
                centerId = 1L,
                centerName = "Bargarh Central Paddy Mandi (PPC-01)",
                cropVariety = "Paddy (Grade A)",
                estimatedYieldQuintals = 45.0,
                vehicleType = "Tractor Trolley",
                vehicleNumber = "OD-17-TR-6582",
                bookedDate = "Today, Slot #4",
                slotTimeWindow = "10:00 AM - 12:00 PM",
                recommendedDepartureTime = "09:15 AM",
                assignedGate = "Gate 2 (Tractor Entry)",
                status = "CONFIRMED",
                tokensAheadInQueue = 4,
                estimatedWaitMinutes = 32,
                moisturePercent = 14.2,
                impuritiesPercent = 1.0,
                qualityGrade = "Grade A (FAQ Standard)",
                mspRatePerQuintal = 2320.0,
                grossWeightKg = 6850.0,
                tareWeightKg = 2350.0,
                netWeightQuintals = 45.0,
                totalAmount = 104400.0,
                weighmentSlipNumber = "WS-2026-99120",
                dbtStatus = "INITIATED",
                dbtTransactionId = "UTR-982734190123",
                dbtBankRef = "SBI A/C •••• 4821",
                createdAtTimestamp = System.currentTimeMillis() - 1000 * 60 * 60
            )
            dao.insertBooking(initialBooking)

            // Initial timely SMS & App alerts
            val alerts = listOf(
                NotificationAlert(
                    id = 1L,
                    bookingId = 1L,
                    tokenNumber = "HH-108",
                    title = "Token Generated & Slot Confirmed",
                    message = "Your Paddy delivery slot is confirmed at Bargarh Central PPC for 10:00 AM - 12:00 PM today.",
                    smsText = "[GOV-PADDY] Dear Rameshwar Patel, Token #HH-108 issued for 45 Qtls Grade A Paddy. Date: Today 10:00 AM - 12:00 PM at Gate 2. Entry allowed only with this SMS.",
                    category = "SLOT",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 45,
                    isRead = true
                ),
                NotificationAlert(
                    id = 2L,
                    bookingId = 1L,
                    tokenNumber = "HH-108",
                    title = "Timely Departure Advisory",
                    message = "Please leave your village by 09:15 AM to arrive without waiting in traffic. Current gate queue is 4 vehicles ahead.",
                    smsText = "[GOV-PADDY] Token #HH-108: Recommended departure time is 09:15 AM. 4 vehicles ahead. Est wait at Gate 2 is 32 mins. Avoid open idling.",
                    category = "ARRIVAL",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 20,
                    isRead = false
                ),
                NotificationAlert(
                    id = 3L,
                    bookingId = 1L,
                    tokenNumber = "HH-108",
                    title = "Live Queue Update: Token HH-104 Serving",
                    message = "Weighbridge 1 is now processing Token #HH-104. You are next in sequence after 3 vehicles.",
                    smsText = "[GOV-PADDY] Live Update: Token #HH-104 is on the Weighbridge. Your Token #HH-108 is currently 4th in line at Gate 2.",
                    category = "QUEUE",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 5,
                    isRead = false
                )
            )
            for (alert in alerts) {
                dao.insertNotification(alert)
            }
        }
    }
}
