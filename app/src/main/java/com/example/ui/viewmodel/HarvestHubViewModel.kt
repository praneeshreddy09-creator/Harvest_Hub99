package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.FarmerProfile
import com.example.data.local.entity.NotificationAlert
import com.example.data.local.entity.ProcurementCenter
import com.example.data.local.entity.SlotBooking
import com.example.data.repository.HarvestHubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    HINDI("hi", "Hindi", "हिन्दी"),
    TELUGU("te", "Telugu", "తెలుగు")
}

class HarvestHubViewModel(
    private val repository: HarvestHubRepository
) : ViewModel() {

    val farmerProfile: StateFlow<FarmerProfile?> = repository.farmerProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val centers: StateFlow<List<ProcurementCenter>> = repository.centers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBookings: StateFlow<List<SlotBooking>> = repository.allBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val latestBooking: StateFlow<SlotBooking?> = repository.latestBooking
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val notifications: StateFlow<List<NotificationAlert>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadCount: StateFlow<Int> = repository.unreadCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _selectedLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val selectedLanguage: StateFlow<AppLanguage> = _selectedLanguage.asStateFlow()

    private val _activeTab = MutableStateFlow(0) // 0: Home/Queue, 1: Book Slot, 2: Testing & Weigh, 3: DBT & SMS
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun selectLanguage(language: AppLanguage) {
        _selectedLanguage.value = language
    }

    fun setActiveTab(tab: Int) {
        _activeTab.value = tab
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun markNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }

    fun bookSlot(
        centerId: Long,
        centerName: String,
        cropVariety: String,
        estimatedYield: Double,
        vehicleType: String,
        vehicleNumber: String,
        date: String,
        timeSlot: String,
        onSuccess: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val id = repository.bookSlot(
                centerId = centerId,
                centerName = centerName,
                cropVariety = cropVariety,
                estimatedYield = estimatedYield,
                vehicleType = vehicleType,
                vehicleNumber = vehicleNumber,
                date = date,
                timeSlot = timeSlot
            )
            _userMessage.value = "Slot booked successfully! SMS Token has been sent to your phone."
            _activeTab.value = 0 // Go to Home/Queue tab
            onSuccess(id)
        }
    }

    fun advanceLifecycle(booking: SlotBooking) {
        viewModelScope.launch {
            val updated = repository.advanceBookingLifecycle(booking)
            val msg = when (updated.status) {
                "ARRIVED" -> "Vehicle checked-in at Gate! SMS alert sent."
                "TESTED" -> "Digital Moisture & Assaying complete! Grade A approved."
                "WEIGHED" -> "Digital weighment recorded! Slip generated & sent."
                "COMPLETED" -> "DBT payment ₹${updated.totalAmount.toInt()} transferred to bank!"
                else -> "Status updated."
            }
            _userMessage.value = msg
        }
    }

    fun simulateQueueStep(booking: SlotBooking) {
        viewModelScope.launch {
            val updated = repository.decrementQueue(booking)
            if (updated.tokensAheadInQueue == 0) {
                _userMessage.value = "You are now at the front of the queue at Gate!"
            } else {
                _userMessage.value = "Queue updated: ${updated.tokensAheadInQueue} vehicles ahead (Est. ${updated.estimatedWaitMinutes} mins)."
            }
        }
    }

    fun updateProfile(profile: FarmerProfile) {
        viewModelScope.launch {
            repository.updateProfile(profile)
            _userMessage.value = "Farmer profile updated."
        }
    }

    companion object {
        fun provideFactory(repository: HarvestHubRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HarvestHubViewModel(repository) as T
                }
            }
    }
}
