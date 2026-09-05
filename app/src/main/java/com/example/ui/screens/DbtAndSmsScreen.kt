package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.FarmerProfile
import com.example.data.local.entity.NotificationAlert
import com.example.data.local.entity.SlotBooking
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppLanguage

@Composable
fun DbtAndSmsScreen(
    farmerProfile: FarmerProfile?,
    booking: SlotBooking?,
    notifications: List<NotificationAlert>,
    language: AppLanguage,
    onMarkAllRead: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        item {
            Text(
                text = HarvestHubStrings.get("tab_dbt", language),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Track direct bank transfers and review timely SMS updates sent to your phone.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Direct Benefit Transfer (DBT) Tracker Card
        if (booking != null) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(StatusGreenBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.AccountBalance,
                                        contentDescription = null,
                                        tint = StatusGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = HarvestHubStrings.get("dbt_tracker", language),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Direct Benefit Transfer (24-48 Hours)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            val isDisbursed = booking.dbtStatus == "DISBURSED"
                            Surface(
                                color = if (isDisbursed) StatusGreenBg else StatusAmberBg,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = booking.dbtStatus,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDisbursed) StatusGreen else StatusAmber
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Amount Banner
                        Surface(
                            color = StatusGreenBg.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Approved Procurement Value",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF14532D)
                                    )
                                    Text(
                                        text = "₹${booking.totalAmount.toInt()}",
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = StatusGreen
                                        )
                                    )
                                }

                                Text(
                                    text = "${booking.netWeightQuintals} Qtl @ ₹${booking.mspRatePerQuintal.toInt()}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color(0xFF14532D)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Bank Account & Aadhaar Link Details
                        Surface(
                            color = Color(0xFFF8FAF8),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                DbtDetailRow(
                                    label = "Beneficiary Farmer",
                                    value = farmerProfile?.fullName ?: "Rameshwar Patel"
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0xFFE2E8E0))
                                DbtDetailRow(
                                    label = "Aadhaar Linked Account",
                                    value = "${farmerProfile?.bankName ?: "SBI"} (${farmerProfile?.bankAccountMasked ?: "•••• 4821"})"
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0xFFE2E8E0))
                                DbtDetailRow(
                                    label = "IFSC Code",
                                    value = farmerProfile?.ifscCode ?: "SBIN0001243"
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0xFFE2E8E0))
                                DbtDetailRow(
                                    label = "Transaction UTR Reference",
                                    value = booking.dbtTransactionId,
                                    isMonospace = true
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Government DBT Mandate: Transferred directly with zero commission cuts.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Live SMS Messages Feed Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Sms, contentDescription = null, tint = HarvestGreenPrimary)
                    Text(
                        text = HarvestHubStrings.get("recent_sms", language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                TextButton(
                    onClick = onMarkAllRead,
                    modifier = Modifier.testTag("mark_sms_read_button")
                ) {
                    Text("Mark Read", color = HarvestGreenPrimary)
                }
            }
        }

        // List of SMS alerts
        if (notifications.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No SMS messages yet.", color = Color.Gray)
                    }
                }
            }
        } else {
            items(notifications, key = { it.id }) { alert ->
                SmsMessageCard(alert = alert)
            }
        }
    }
}

@Composable
fun DbtDetailRow(
    label: String,
    value: String,
    isMonospace: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
