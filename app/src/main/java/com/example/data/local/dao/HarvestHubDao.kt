package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.FarmerProfile
import com.example.data.local.entity.NotificationAlert
import com.example.data.local.entity.ProcurementCenter
import com.example.data.local.entity.SlotBooking
import kotlinx.coroutines.flow.Flow

@Dao
interface HarvestHubDao {

    // Farmer Profile
    @Query("SELECT * FROM farmer_profiles WHERE id = 1 LIMIT 1")
    fun getFarmerProfile(): Flow<FarmerProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarmerProfile(profile: FarmerProfile)

    // Procurement Centers
    @Query("SELECT * FROM procurement_centers ORDER BY name ASC")
    fun getAllCenters(): Flow<List<ProcurementCenter>>

    @Query("SELECT * FROM procurement_centers WHERE id = :id LIMIT 1")
    fun getCenterById(id: Long): Flow<ProcurementCenter?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCenters(centers: List<ProcurementCenter>)

    @Update
    suspend fun updateCenter(center: ProcurementCenter)

    // Slot Bookings
    @Query("SELECT * FROM slot_bookings ORDER BY id DESC")
    fun getAllBookings(): Flow<List<SlotBooking>>

    @Query("SELECT * FROM slot_bookings WHERE id = :id LIMIT 1")
    fun getBookingById(id: Long): Flow<SlotBooking?>

    @Query("SELECT * FROM slot_bookings ORDER BY id DESC LIMIT 1")
    fun getLatestBooking(): Flow<SlotBooking?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: SlotBooking): Long

    @Update
    suspend fun updateBooking(booking: SlotBooking)

    @Query("DELETE FROM slot_bookings WHERE id = :id")
    suspend fun deleteBooking(id: Long)

    // Notification Alerts (Live SMS & Updates)
    @Query("SELECT * FROM notification_alerts ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationAlert>>

    @Query("SELECT COUNT(*) FROM notification_alerts WHERE isRead = 0")
    fun getUnreadNotificationCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationAlert): Long

    @Query("UPDATE notification_alerts SET isRead = 1 WHERE isRead = 0")
    suspend fun markAllNotificationsRead()
}
