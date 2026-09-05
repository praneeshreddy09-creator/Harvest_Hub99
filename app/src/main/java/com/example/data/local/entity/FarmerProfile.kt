package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farmer_profiles")
data class FarmerProfile(
    @PrimaryKey val id: Long = 1L,
    val fullName: String,
    val phone: String,
    val aadhaarMasked: String,
    val farmerId: String,
    val village: String,
    val district: String,
    val state: String,
    val landAreaAcres: Double,
    val bankName: String,
    val bankAccountMasked: String,
    val ifscCode: String
)
