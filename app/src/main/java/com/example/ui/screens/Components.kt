package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.FarmerProfile
import com.example.data.local.entity.NotificationAlert
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppLanguage
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HarvestHubTopBar(
    currentLanguage: AppLanguage,
    unreadCount: Int,
    onLanguageSelected: (AppLanguage) -> Unit,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLangMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BentoPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Agriculture,
                        contentDescription = "HarvestHub Logo",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = "HarvestHub",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = BentoTextPrimary
                    )
                    Text(
                        text = HarvestHubStrings.get("app_subtitle", currentLanguage),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp
                        ),
                        color = BentoTextMuted
                    )
                }
            }
        },
        actions = {
            // Language selector pill
            Box {
                OutlinedButton(
                    onClick = { showLangMenu = true },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, BentoBorder),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = BentoCardWhite),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("language_selector_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        modifier = Modifier.size(16.dp),
                        tint = BentoPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = currentLanguage.nativeName,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = BentoPrimary
                    )
                }

                DropdownMenu(
                    expanded = showLangMenu,
                    onDismissRequest = { showLangMenu = false }
                ) {
                    AppLanguage.values().forEach { lang ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(lang.nativeName, fontWeight = if (lang == currentLanguage) FontWeight.Bold else FontWeight.Normal)
                                    Text(" (${lang.displayName})", color = Color.Gray)
                                }
                            },
                            onClick = {
                                onLanguageSelected(lang)
                                showLangMenu = false
                            },
                            leadingIcon = {
                                if (lang == currentLanguage) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = BentoPrimary)
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Notification Bell with badge in Bento white capsule
            Surface(
                shape = CircleShape,
                color = BentoCardWhite,
                border = BorderStroke(1.dp, BentoBorder),
                modifier = Modifier.size(38.dp)
            ) {
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier.testTag("notification_bell_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadCount > 0) {
                                Badge(
                                    containerColor = BentoPrimary,
                                    contentColor = Color.White
                                ) {
                                    Text("$unreadCount")
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "SMS & App Notifications",
                            tint = BentoTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BentoBackground
        ),
        modifier = modifier
    )
}

@Composable
fun FarmerProfileHeaderCard(
    profile: FarmerProfile?,
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
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(BentoContainerLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Farmer",
                        tint = BentoPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Column {
                    Text(
                        text = "${HarvestHubStrings.get("greeting", language)} ${profile?.fullName ?: "Farmer"}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = BentoTextPrimary
                    )
                    Text(
                        text = "ID: ${profile?.farmerId ?: "OD-PPC-2026-9812"} • ${profile?.village ?: "Attabira"}, ${profile?.district ?: "Bargarh"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = BentoTextMuted
                    )
                }
            }

            Surface(
                color = BentoPrimary,
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "${profile?.landAreaAcres ?: 4.5} Acres",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun QueueProgressTracker(
    currentStatus: String,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        "CONFIRMED" to HarvestHubStrings.get("step_booked", language),
        "ARRIVED" to HarvestHubStrings.get("step_arrived", language),
        "TESTED" to HarvestHubStrings.get("step_tested", language),
        "WEIGHED" to HarvestHubStrings.get("step_weighed", language),
        "COMPLETED" to HarvestHubStrings.get("step_paid", language)
    )

    val currentIndex = when (currentStatus) {
        "ARRIVED" -> 1
        "TESTED" -> 2
        "WEIGHED" -> 3
        "COMPLETED" -> 4
        else -> 0
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardWhite),
        border = BorderStroke(1.dp, BentoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Procurement Milestones",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = BentoTextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                steps.forEachIndexed { index, pair ->
                    val isDone = index <= currentIndex
                    val isCurrent = index == currentIndex

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isDone) BentoPrimary else BentoContainerLight
                                )
                                .border(
                                    width = if (isCurrent) 2.dp else 0.dp,
                                    color = if (isCurrent) BentoPrimary else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (index < currentIndex) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDone) Color.White else BentoTextSecondary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = pair.second,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                textAlign = TextAlign.Center
                            ),
                            maxLines = 2,
                            color = if (isCurrent) BentoPrimary else BentoTextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DigitalMoistureMeterGauge(
    moisturePercent: Double,
    maxThreshold: Double = 17.0,
    modifier: Modifier = Modifier
) {
    val isApproved = moisturePercent <= maxThreshold

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = BentoCardWhite),
        border = BorderStroke(1.dp, BentoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Electronic Moisture Meter",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = BentoTextPrimary
                )
                Surface(
                    color = if (isApproved) BentoContainerLight else StatusAmberBg,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, if (isApproved) BentoBorder else StatusAmber.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = if (isApproved) "APPROVED (<17%)" else "MOISTURE HIGH",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isApproved) BentoPrimary else StatusAmber
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gauge Drawing
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(140.dp)
            ) {
                Canvas(modifier = Modifier.size(130.dp)) {
                    val strokeWidth = 14.dp.toPx()
                    val diameter = size.minDimension - strokeWidth
                    val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                    val arcSize = Size(diameter, diameter)

                    // Background track (180 to 360)
                    drawArc(
                        color = BentoContainerLight,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Safe threshold zone limit
                    val progressSweep = (moisturePercent.toFloat() / 25f).coerceIn(0f, 1f) * 180f

                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                BentoPrimary,
                                Color(0xFFF59E0B),
                                Color(0xFFEF4444)
                            )
                        ),
                        startAngle = 180f,
                        sweepAngle = progressSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(
                        text = "$moisturePercent%",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isApproved) BentoPrimary else StatusAmber
                        )
                    )
                    Text(
                        text = "Standard Limit: 17.0%",
                        style = MaterialTheme.typography.labelSmall,
                        color = BentoTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tamper-Proof Auto-Assaying: Zero deduction applied for grade compliance.",
                style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center),
                color = BentoTextMuted
            )
        }
    }
}

@Composable
fun BarcodeTokenVisual(
    tokenNumber: String,
    gate: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BentoContainerLight)
            .border(BorderStroke(1.dp, BentoBorder), RoundedCornerShape(20.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Simulated Barcode lines
        Row(
            modifier = Modifier
                .height(36.dp)
                .fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(3, 1, 4, 2, 1, 5, 2, 4, 1, 3, 2, 1, 4, 3, 1, 2, 5, 1, 3, 2, 4, 1).forEach { w ->
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(w.dp)
                        .background(BentoPrimaryDark)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "* $tokenNumber - $gate *",
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = BentoPrimaryDark
        )
    }
}

@Composable
fun SmsMessageCard(
    alert: NotificationAlert,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val formattedTime = dateFormat.format(Date(alert.timestamp))

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (alert.category == "PAYMENT") BentoContainerLight else BentoCardWhite
        ),
        border = BorderStroke(1.dp, BentoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.fillMaxWidth()
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
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                when (alert.category) {
                                    "PAYMENT" -> BentoPrimary
                                    "ARRIVAL" -> HarvestAmberPrimary
                                    else -> BentoPrimary
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (alert.category) {
                                "SLOT" -> Icons.Default.ConfirmationNumber
                                "ARRIVAL" -> Icons.Default.DirectionsCar
                                "QUEUE" -> Icons.Default.AccessTime
                                "GATE_ENTRY" -> Icons.Default.SensorDoor
                                "TESTING" -> Icons.Default.CheckCircle
                                "WEIGHMENT" -> Icons.Default.Scale
                                "PAYMENT" -> Icons.Default.AccountBalance
                                else -> Icons.Default.Sms
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = alert.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = BentoTextPrimary
                    )
                }

                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = BentoTextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // SMS Bubble appearance
            Surface(
                color = BentoBackground,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BentoBorder.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "SMS",
                        tint = BentoTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = alert.smsText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        ),
                        color = BentoTextPrimary
                    )
                }
            }
        }
    }
}
