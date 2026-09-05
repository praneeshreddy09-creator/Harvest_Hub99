package com.example.ui.screens

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.SlotBooking
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppLanguage

@Composable
fun QualityWeighmentScreen(
    booking: SlotBooking?,
    language: AppLanguage,
    onAdvanceStage: (SlotBooking) -> Unit,
    modifier: Modifier = Modifier
) {
    if (booking == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No active booking found. Please book a slot first.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        item {
            Text(
                text = HarvestHubStrings.get("tab_assay", language),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Automated digital testing & weighbridge slips to eliminate middlemen deductions.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Active Token summary ribbon
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = HarvestGreenContainer.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Token: ${booking.tokenNumber}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = HarvestOnGreenContainer
                        )
                        Text(
                            text = "${booking.cropVariety} • Vehicle: ${booking.vehicleNumber}",
                            style = MaterialTheme.typography.bodySmall,
                            color = HarvestOnGreenContainer.copy(alpha = 0.8f)
                        )
                    }

                    Surface(
                        color = HarvestGreenPrimary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = booking.status,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Digital Moisture Meter Gauge
        item {
            DigitalMoistureMeterGauge(
                moisturePercent = booking.moisturePercent,
                maxThreshold = 17.0
            )
        }

        // Grain Quality Assaying Parameters
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Assaying & Grading Parameters",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Icon(Icons.Default.Verified, contentDescription = null, tint = StatusGreen)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QualityParameterCard(
                            title = "Foreign Matter",
                            value = "${booking.impuritiesPercent}%",
                            limit = "Limit: < 2.0%",
                            status = "PASS",
                            isOk = true,
                            modifier = Modifier.weight(1f)
                        )
                        QualityParameterCard(
                            title = "Discolored / Immature",
                            value = "2.1%",
                            limit = "Limit: < 3.0%",
                            status = "PASS",
                            isOk = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    QualityParameterCard(
                        title = "Final Grade Result",
                        value = booking.qualityGrade,
                        limit = "Official MSP: ₹${booking.mspRatePerQuintal.toInt()} / Quintal",
                        status = "APPROVED",
                        isOk = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Digital Weighbridge Slip
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
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
                            Icon(Icons.Default.Scale, contentDescription = null, tint = HarvestGreenPrimary)
                            Text(
                                text = HarvestHubStrings.get("digital_weighment_slip", language),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Text(
                            text = booking.weighmentSlipNumber,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = HarvestGreenPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Weighbridge Breakdown Table
                    Surface(
                        color = Color(0xFFF8FAF8),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            WeightSlipRow(label = "Gross Weight (Loaded Vehicle)", value = "${booking.grossWeightKg.toInt()} kg")
                            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0xFFE2E8E0))
                            WeightSlipRow(label = "Tare Weight (Empty Vehicle)", value = "${booking.tareWeightKg.toInt()} kg")
                            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0xFFE2E8E0))
                            WeightSlipRow(
                                label = "Net Paddy Weight",
                                value = "${booking.netWeightQuintals} Quintals (${(booking.netWeightQuintals * 100).toInt()} kg)",
                                isHighlight = true
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0xFFE2E8E0))
                            WeightSlipRow(label = "Rate per Quintal (MSP)", value = "₹${booking.mspRatePerQuintal.toInt()}")
                            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0xFFE2E8E0))
                            WeightSlipRow(
                                label = "Total Amount Payable",
                                value = "₹${booking.totalAmount.toInt()}",
                                isHighlight = true,
                                valueColor = StatusGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    BarcodeTokenVisual(
                        tokenNumber = booking.weighmentSlipNumber,
                        gate = "WEIGHBRIDGE-01"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Scale Operator: Auto-Sync Sensor",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = "Tamper-Proof Digital Seal",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = StatusGreen
                            )
                        )
                    }
                }
            }
        }

        // Action to advance state if not completed
        if (booking.status != "COMPLETED") {
            item {
                Button(
                    onClick = { onAdvanceStage(booking) },
                    colors = ButtonDefaults.buttonColors(containerColor = HarvestGreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("advance_testing_stage_button")
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Advance to Next Stage (${when (booking.status) {
                            "CONFIRMED" -> "Gate Check-In"
                            "ARRIVED" -> "Perform Quality Test"
                            "TESTED" -> "Record Digital Weight"
                            "WEIGHED" -> "Trigger DBT Payment"
                            else -> "Next"
                        }})",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun QualityParameterCard(
    title: String,
    value: String,
    limit: String,
    status: String,
    isOk: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFFF8FAF8),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8E0))
        ),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    color = if (isOk) StatusGreenBg else StatusAmberBg,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = status,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOk) StatusGreen else StatusAmber
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = limit,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = Color.Gray
            )
        }
    }
}

@Composable
fun WeightSlipRow(
    label: String,
    value: String,
    isHighlight: Boolean = false,
    valueColor: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isHighlight) MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = if (isHighlight) MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = if (valueColor != Color.Unspecified) valueColor else MaterialTheme.colorScheme.onSurface
        )
    }
}
