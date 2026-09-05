package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.FarmerProfile
import com.example.data.local.entity.NotificationAlert
import com.example.data.local.entity.ProcurementCenter
import com.example.data.local.entity.SlotBooking
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppLanguage

@Composable
fun HomeScreen(
    farmerProfile: FarmerProfile?,
    activeBooking: SlotBooking?,
    centers: List<ProcurementCenter>,
    latestNotification: NotificationAlert?,
    language: AppLanguage,
    onBookSlotClick: () -> Unit,
    onSimulateQueueStep: (SlotBooking) -> Unit,
    onAdvanceLifecycle: (SlotBooking) -> Unit,
    onViewWeighmentDetails: () -> Unit,
    onViewDbtDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
    ) {
        // Farmer profile Bento card
        item {
            FarmerProfileHeaderCard(profile = farmerProfile, language = language)
        }

        // MSP Benchmark Rate Bento Tile
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoContainerLight),
                border = BorderStroke(1.dp, BentoBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = BentoPrimary,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "GOVT MSP 2026",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "Paddy Grade A",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = BentoTextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "₹2,320 / Qtl",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = BentoPrimaryDark
                            )
                        )
                        Text(
                            text = "Assured Direct Benefit Transfer to Bank",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = BentoTextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(BentoContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Grass,
                            contentDescription = "Paddy Crop",
                            tint = BentoPrimary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }

        // Active Token & Live Queue Bento Card
        if (activeBooking != null) {
            item {
                ActiveTokenLiveQueueCard(
                    booking = activeBooking,
                    language = language,
                    onSimulateQueueStep = { onSimulateQueueStep(activeBooking) },
                    onAdvanceLifecycle = { onAdvanceLifecycle(activeBooking) },
                    onViewWeighmentDetails = onViewWeighmentDetails,
                    onViewDbtDetails = onViewDbtDetails
                )
            }

            // Milestone step progress
            item {
                QueueProgressTracker(currentStatus = activeBooking.status, language = language)
            }
        } else {
            // Empty state Bento card
            item {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoCardWhite),
                    border = BorderStroke(1.dp, BentoBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(BentoContainerLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = BentoPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = HarvestHubStrings.get("no_active_booking", language),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = BentoTextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onBookSlotClick,
                            colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.testTag("book_slot_button_empty_state")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = HarvestHubStrings.get("book_new_slot", language),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Latest SMS Alert Bento Card (Timely updates sent to phone)
        if (latestNotification != null) {
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoCardWhite),
                    border = BorderStroke(1.dp, BentoBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(BentoPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Sms,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = HarvestHubStrings.get("recent_sms", language),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = BentoTextPrimary
                                    )
                                )
                            }
                            Surface(
                                color = BentoContainerLight,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "SMS DELIVERED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoPrimary
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = BentoBackground,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, BentoBorder.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = latestNotification.smsText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                ),
                                color = BentoTextPrimary,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }

        // Live Mandi Status Bento Card
        item {
            val center = centers.firstOrNull()
            if (center != null) {
                MandiLiveStatusCard(center = center, language = language)
            }
        }

        // Quick Actions Bento Tile
        item {
            Button(
                onClick = onBookSlotClick,
                colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("book_another_slot_button")
            ) {
                Icon(Icons.Default.AddCircleOutline, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = HarvestHubStrings.get("book_new_slot", language),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun ActiveTokenLiveQueueCard(
    booking: SlotBooking,
    language: AppLanguage,
    onSimulateQueueStep: () -> Unit,
    onAdvanceLifecycle: () -> Unit,
    onViewWeighmentDetails: () -> Unit,
    onViewDbtDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardWhite),
        border = BorderStroke(1.dp, BentoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Token Number & Variety
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = BentoPrimary,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = booking.tokenNumber,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 1.sp
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = booking.cropVariety,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = BentoTextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${booking.estimatedYieldQuintals} Quintals • ${booking.vehicleType}",
                        style = MaterialTheme.typography.bodySmall,
                        color = BentoTextMuted
                    )
                }

                // Status Pill
                val statusColor = when (booking.status) {
                    "COMPLETED" -> BentoPrimary
                    "ARRIVED", "TESTED", "WEIGHED" -> Color(0xFF2563EB)
                    else -> HarvestAmberPrimary
                }
                Surface(
                    color = BentoContainerLight,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BentoBorder)
                ) {
                    Text(
                        text = booking.status,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Timely Arrival Recommendation Bento Tile (MANDATE)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BentoContainerLight),
                border = BorderStroke(1.dp, BentoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(HarvestAmberPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Text(
                            text = HarvestHubStrings.get("arrival_recommendation", language),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary
                            )
                        )
                        Text(
                            text = "${HarvestHubStrings.get("leave_by", language)}: ${booking.recommendedDepartureTime} for ${booking.slotTimeWindow}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = BentoTextPrimary
                            )
                        )
                        Text(
                            text = "Destination: ${booking.centerName} (${booking.assignedGate})",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = BentoTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bento 2-Column Grid: Queue Telemetry
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Tokens ahead
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoBackground),
                    border = BorderStroke(1.dp, BentoBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = HarvestHubStrings.get("tokens_ahead", language),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = BentoTextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${booking.tokensAheadInQueue} Vehicles",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (booking.tokensAheadInQueue <= 1) BentoPrimary else BentoTextPrimary
                            )
                        )
                    }
                }

                // Estimated Wait
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoBackground),
                    border = BorderStroke(1.dp, BentoBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = HarvestHubStrings.get("est_wait", language),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = BentoTextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${booking.estimatedWaitMinutes} Mins",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (booking.estimatedWaitMinutes < 15) BentoPrimary else BentoTextPrimary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Barcode view for gate entry
            BarcodeTokenVisual(
                tokenNumber = booking.tokenNumber,
                gate = booking.assignedGate
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive simulation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onSimulateQueueStep,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, BentoBorder),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = BentoBackground),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("simulate_queue_button")
                ) {
                    Icon(
                        Icons.Default.FastForward,
                        contentDescription = null,
                        tint = BentoPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = HarvestHubStrings.get("simulate_queue", language),
                        style = MaterialTheme.typography.labelMedium,
                        color = BentoPrimary
                    )
                }

                Button(
                    onClick = onAdvanceLifecycle,
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("advance_stage_button")
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = HarvestHubStrings.get("advance_stage", language),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun MandiLiveStatusCard(
    center: ProcurementCenter,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardWhite),
        border = BorderStroke(1.dp, BentoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = HarvestHubStrings.get("mandi_status", language),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = BentoTextPrimary
                )
                Surface(
                    color = BentoContainerLight,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, BentoBorder)
                ) {
                    Text(
                        text = "LIVE & OPEN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = BentoPrimary
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = center.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = BentoTextPrimary
            )
            Text(
                text = center.address,
                style = MaterialTheme.typography.bodySmall,
                color = BentoTextMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Capacity Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Daily Yard Capacity: ${center.dailyCapacityQuintals.toInt()} Qtl",
                    style = MaterialTheme.typography.labelSmall,
                    color = BentoTextMuted
                )
                Text(
                    text = "${center.currentSlotOccupancyPercent}% Booked",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = BentoPrimary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { center.currentSlotOccupancyPercent / 100f },
                color = BentoPrimary,
                trackColor = BentoContainerLight,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = null,
                        tint = BentoPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Toll-Free: ${center.contactHelpline}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = BentoTextPrimary
                    )
                }

                Surface(
                    color = BentoContainerLight,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Serving: ${center.currentServingToken}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = BentoPrimary
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}
