package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.HarvestHubDatabase
import com.example.data.repository.HarvestHubRepository
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.HarvestHubViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = HarvestHubDatabase.getDatabase(this, applicationScope)
        val repository = HarvestHubRepository(database.harvestHubDao())
        val viewModelFactory = HarvestHubViewModel.provideFactory(repository)

        setContent {
            HarvestHubTheme {
                val viewModel: HarvestHubViewModel = viewModel(factory = viewModelFactory)

                HarvestHubApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun HarvestHubApp(viewModel: HarvestHubViewModel) {
    val farmerProfile by viewModel.farmerProfile.collectAsStateWithLifecycle()
    val centers by viewModel.centers.collectAsStateWithLifecycle()
    val latestBooking by viewModel.latestBooking.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = msg,
                    duration = SnackbarDuration.Short
                )
                viewModel.clearUserMessage()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HarvestHubTopBar(
                currentLanguage = currentLanguage,
                unreadCount = unreadCount,
                onLanguageSelected = { viewModel.selectLanguage(it) },
                onNotificationClick = {
                    viewModel.setActiveTab(3) // Navigate to DBT & SMS notifications tab
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = BentoCardWhite,
                contentColor = BentoTextPrimary,
                modifier = Modifier
                    .testTag("bottom_nav_bar")
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .border(
                        width = 1.dp,
                        color = BentoBorder,
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    )
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            ) {
                val navItemColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BentoPrimary,
                    selectedTextColor = BentoPrimary,
                    indicatorColor = BentoContainer,
                    unselectedIconColor = BentoTextMuted,
                    unselectedTextColor = BentoTextMuted
                )

                // Tab 0: Live Queue
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { viewModel.setActiveTab(0) },
                    colors = navItemColors,
                    icon = {
                        Icon(
                            if (activeTab == 0) Icons.Filled.AccessTimeFilled else Icons.Outlined.AccessTime,
                            contentDescription = "Live Queue"
                        )
                    },
                    label = { Text(HarvestHubStrings.get("tab_queue", currentLanguage)) },
                    modifier = Modifier.testTag("nav_tab_queue")
                )

                // Tab 1: Book Slot
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { viewModel.setActiveTab(1) },
                    colors = navItemColors,
                    icon = {
                        Icon(
                            if (activeTab == 1) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                            contentDescription = "Book Slot"
                        )
                    },
                    label = { Text(HarvestHubStrings.get("tab_book", currentLanguage)) },
                    modifier = Modifier.testTag("nav_tab_book")
                )

                // Tab 2: Assaying & Weighment
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { viewModel.setActiveTab(2) },
                    colors = navItemColors,
                    icon = {
                        Icon(
                            if (activeTab == 2) Icons.Filled.Scale else Icons.Outlined.Scale,
                            contentDescription = "Quality & Weight"
                        )
                    },
                    label = { Text(HarvestHubStrings.get("tab_assay", currentLanguage)) },
                    modifier = Modifier.testTag("nav_tab_assay")
                )

                // Tab 3: DBT & SMS
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = {
                        viewModel.setActiveTab(3)
                        viewModel.markNotificationsRead()
                    },
                    colors = navItemColors,
                    icon = {
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge(
                                        containerColor = BentoPrimary,
                                        contentColor = Color.White
                                    ) { Text("$unreadCount") }
                                }
                            }
                        ) {
                            Icon(
                                if (activeTab == 3) Icons.Filled.AccountBalance else Icons.Outlined.AccountBalance,
                                contentDescription = "DBT & SMS"
                            )
                        }
                    },
                    label = { Text(HarvestHubStrings.get("tab_dbt", currentLanguage)) },
                    modifier = Modifier.testTag("nav_tab_dbt")
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                0 -> HomeScreen(
                    farmerProfile = farmerProfile,
                    activeBooking = latestBooking,
                    centers = centers,
                    latestNotification = notifications.firstOrNull(),
                    language = currentLanguage,
                    onBookSlotClick = { viewModel.setActiveTab(1) },
                    onSimulateQueueStep = { viewModel.simulateQueueStep(it) },
                    onAdvanceLifecycle = { viewModel.advanceLifecycle(it) },
                    onViewWeighmentDetails = { viewModel.setActiveTab(2) },
                    onViewDbtDetails = { viewModel.setActiveTab(3) }
                )

                1 -> SlotBookingScreen(
                    centers = centers,
                    language = currentLanguage,
                    onBookSlot = { cId, cName, variety, yield, vehicle, vNum, date, timeSlot ->
                        viewModel.bookSlot(
                            centerId = cId,
                            centerName = cName,
                            cropVariety = variety,
                            estimatedYield = yield,
                            vehicleType = vehicle,
                            vehicleNumber = vNum,
                            date = date,
                            timeSlot = timeSlot,
                            onSuccess = {
                                viewModel.setActiveTab(0)
                            }
                        )
                    }
                )

                2 -> QualityWeighmentScreen(
                    booking = latestBooking,
                    language = currentLanguage,
                    onAdvanceStage = { viewModel.advanceLifecycle(it) }
                )

                3 -> DbtAndSmsScreen(
                    farmerProfile = farmerProfile,
                    booking = latestBooking,
                    notifications = notifications,
                    language = currentLanguage,
                    onMarkAllRead = { viewModel.markNotificationsRead() }
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}


