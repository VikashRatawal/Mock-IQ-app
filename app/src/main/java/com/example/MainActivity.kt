package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.*
import com.example.ui.MockTrackerViewModel
import com.example.ui.theme.MyApplicationTheme

// Premium Dark Theme Palette
val DarkBg = Color(0xFF0D0F14)
val DarkSurface = Color(0xFF161B22)
val DarkCard = Color(0xFF21262D)
val DarkBorder = Color(0xFF30363D)
val AccentOrange = Color(0xFFFF7E40)
val AccentOrangeDim = Color(0xFFFF7E40).copy(alpha = 0.15f)
val StatusGreen = Color(0xFF22C55E)
val StatusGreenDim = Color(0xFF22C55E).copy(alpha = 0.12f)
val StatusRed = Color(0xFFEF4444)
val StatusRedDim = Color(0xFFEF4444).copy(alpha = 0.12f)
val StatusAmber = Color(0xFFF59E0B)
val StatusAmberDim = Color(0xFFF59E0B).copy(alpha = 0.12f)
val StatusBlue = Color(0xFF3B82F6)
val StatusBlueDim = Color(0xFF3B82F6).copy(alpha = 0.12f)
val MutedText = Color(0xFF8B949E)
val LightText = Color(0xFFF0F2FA)

data class TabItem(val name: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val isFab: Boolean = false)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBg
                ) {
                    SSCAppMainScreen()
                }
            }
        }
    }
}

@Composable
fun SSCAppMainScreen() {
    val context = LocalContext.current
    val viewModel: MockTrackerViewModel = viewModel()

    // Database flows
    val mockTests by viewModel.mockTests.collectAsStateWithLifecycle()
    val questions by viewModel.questions.collectAsStateWithLifecycle()
    val customReasonsDb by viewModel.dbCustomReasons.collectAsStateWithLifecycle()
    val customTagsDb by viewModel.dbCustomTags.collectAsStateWithLifecycle()
    val activeMockId by viewModel.activeMockId.collectAsStateWithLifecycle()
    val activeMockQuestions by viewModel.activeMockQuestions.collectAsStateWithLifecycle()

    // Navigation Index
    var currentTab by remember { mutableStateOf("Dashboard") } // Dashboard, Setup, Entry, Questions, Analytics, Revision

    // Fast Paste Bulk text holder
    var fastBulkText by remember { mutableStateOf("") }
    var showFastBulkForm by remember { mutableStateOf(false) }

    // Floating Custom tag addition state
    var showCustomTagEntry by remember { mutableStateOf(false) }
    var customTagText by remember { mutableStateOf("") }

    // Floating Custom reason addition state
    var showCustomReasonEntry by remember { mutableStateOf(false) }
    var customReasonText by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(
                currentTab = currentTab,
                onTabSelected = { tab ->
                    currentTab = tab
                }
            )
        },
        containerColor = DarkBg,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Logo",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "SSC Mock Tracker",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = LightText
                            )
                            Text(
                                text = "CGL & CPO Exam Prep Solution",
                                fontSize = 11.sp,
                                color = MutedText,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Streak indicators
                    val streakValue = calculateStreak(mockTests)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(AccentOrangeDim)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(text = "🔥", fontSize = 12.sp)
                        Text(
                            text = "$streakValue Days Streak",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentOrange
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                "Dashboard" -> {
                    DashboardTab(
                        mockTests = mockTests,
                        questions = questions,
                        onNavigateToSetup = { currentTab = "Setup" },
                        onNavigateToEntry = { currentTab = "Entry" }
                    )
                }
                "Setup" -> {
                    SetupTab(
                        viewModel = viewModel,
                        activeMockId = activeMockId,
                        mockTests = mockTests,
                        onStartEntry = {
                            currentTab = "Entry"
                        }
                    )
                }
                "Entry" -> {
                    if (activeMockId == null) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Warning",
                                tint = StatusAmber,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No Active Mock Session",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = LightText,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Setup a mock session or load a previous session first to start logging questions.",
                                fontSize = 13.sp,
                                color = MutedText,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { currentTab = "Setup" },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
                            ) {
                                Text("Go to Session Setup", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        EntryTab(
                            viewModel = viewModel,
                            activeMockQuestions = activeMockQuestions,
                            customReasonsDb = customReasonsDb,
                            customTagsDb = customTagsDb,
                            showFastBulkForm = showFastBulkForm,
                            onToggleFastBulk = { showFastBulkForm = !showFastBulkForm },
                            fastBulkText = fastBulkText,
                            onFastBulkTextChange = { fastBulkText = it },
                            showCustomReasonEntry = showCustomReasonEntry,
                            onToggleCustomReasonEntry = { showCustomReasonEntry = !showCustomReasonEntry },
                            customReasonText = customReasonText,
                            onCustomReasonTextChange = { customReasonText = it },
                            showCustomTagEntry = showCustomTagEntry,
                            onToggleCustomTagEntry = { showCustomTagEntry = !showCustomTagEntry },
                            customTagText = customTagText,
                            onCustomTagTextChange = { customTagText = it },
                            onGoToQuestions = { currentTab = "Questions" }
                        )
                    }
                }
                "Questions" -> {
                    QuestionsTab(
                        viewModel = viewModel,
                        questions = questions,
                        mockTests = mockTests
                    )
                }
                "Analytics" -> {
                    AnalyticsTab(
                        questions = questions,
                        mockTests = mockTests,
                        viewModel = viewModel
                    )
                }
                "Revision" -> {
                    RevisionTab(
                        questions = questions,
                        mockTests = mockTests,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

// Bottom navigation list
@Composable
fun BottomNavigationBar(
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
    val items = listOf(
        TabItem("Dashboard", Icons.Default.Home),
        TabItem("Setup", Icons.Default.Settings),
        TabItem("Entry", Icons.Default.Add, isFab = true),
        TabItem("Questions", Icons.Default.List),
        TabItem("Analytics", Icons.Default.Star),
        TabItem("Revision", Icons.Default.Info)
    )

    Column {
        Divider(color = DarkBorder, thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .navigationBarsPadding()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                if (item.isFab) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                onClick = { onTabSelected(item.name) },
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (currentTab == item.name) AccentOrange else AccentOrange.copy(alpha = 0.8f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.name,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.name,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (currentTab == item.name) AccentOrange else MutedText
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onTabSelected(item.name) }
                            .padding(vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.name,
                            tint = if (currentTab == item.name) AccentOrange else MutedText,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = item.name,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (currentTab == item.name) AccentOrange else MutedText
                        )
                    }
                }
            }
        }
    }
}

// --- DASHBOARD TAB ---
@Composable
fun DashboardTab(
    mockTests: List<MockTestEntity>,
    questions: List<QuestionEntity>,
    onNavigateToSetup: () -> Unit,
    onNavigateToEntry: () -> Unit
) {
    val scrollState = rememberScrollState()

    // Calculate aggregated metrics
    val totalMocks = mockTests.size
    val totalQs = questions.size
    val completedQs = questions.count { it.status == "Correct" }
    val wrongQs = questions.count { it.status == "Incorrect" }

    val overallAccuracy = if (completedQs + wrongQs > 0) {
        (completedQs.toDouble() / (completedQs + wrongQs).toDouble()) * 100.0
    } else 0.0

    val averageScore = if (totalMocks > 0) mockTests.map { it.score }.average() else 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Daily Progress / Readiness Header
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, DarkBorder),
            shape = RoundedCornerShape(18.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "EXAM READINESS SCORE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedText,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${if (totalMocks > 0) (overallAccuracy * 0.6 + (averageScore / 50.0) * 100.0 * 0.4).toInt() else 0}%",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = AccentOrange
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = getReadinessTip(overallAccuracy, averageScore, totalMocks),
                        fontSize = 12.sp,
                        color = LightText,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Beautiful Custom Ring Indicator using Canvas
                Box(
                    modifier = Modifier.size(76.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val ringProgress = if (totalMocks > 0) (ringProgressHelper(overallAccuracy)).toFloat() else 0f
                    Canvas(modifier = Modifier.size(70.dp)) {
                        drawCircle(
                            color = DarkBorder,
                            style = Stroke(width = 6.dp.toPx())
                        )
                        drawArc(
                            color = AccentOrange,
                            startAngle = -90f,
                            sweepAngle = 360f * ringProgress,
                            useCenter = false,
                            style = Stroke(width = 6.dp.toPx())
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${overallAccuracy.toInt()}%",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = LightText
                        )
                        Text(
                            text = "Accuracy",
                            fontSize = 8.sp,
                            color = MutedText,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Active Tasks Grid / Primary Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onNavigateToSetup,
                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                modifier = Modifier
                    .weight(1.2f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Log", tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Start Mock Log", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Button(
                onClick = onNavigateToEntry,
                colors = ButtonDefaults.buttonColors(containerColor = DarkCard),
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Direct Entry", color = LightText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        // Aggregate Metrics Cards Grid
        Column {
            Text(
                text = "TRACKED PERFORMANCE METRICS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MutedText,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricCard(
                    title = "Avg Raw Score",
                    value = if (totalMocks > 0) String.format("%.1f", averageScore) else "—",
                    color = AccentOrange,
                    label = "Out of 50m",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Total Entered",
                    value = totalQs.toString(),
                    color = StatusBlue,
                    label = "Questions Logged",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Silly Mistakes",
                    value = questions.count { isSillyMistake(it.reason) }.toString(),
                    color = StatusRed,
                    label = "Review List",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Auto recommendations based on Weak Topic detection
        val weakestTopicResult = findWeakestTopic(questions)
        if (weakestTopicResult != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = StatusAmberDim),
                border = BorderStroke(1.dp, StatusAmber.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🚨", fontSize = 18.sp)
                        Text(
                            text = "SMART REVISION RECOMMENDATION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusAmber
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Syllable alarm flagged on Chapter: '${weakestTopicResult.chapter}' (Topic: '${weakestTopicResult.topic}') with Accuracy ${weakestTopicResult.accuracy.toInt()}% under ${weakestTopicResult.total} attempts.",
                        fontSize = 13.sp,
                        color = LightText,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "💡 Action Strategy: Complete the concept flashcard reviews under the Revision Tab, and review formula shortcuts immediately.",
                        fontSize = 12.sp,
                        color = MutedText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Subject Breakdown Panel representation
        Column {
            Text(
                text = "SUBJECT BREAKDOWN (ALL TIME)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MutedText,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "Quantitative Aptitude" to StatusBlue,
                    "Reasoning" to StatusGreen,
                    "English" to StatusAmber,
                    "General Awareness" to Color(0xFF9C27B0)
                ).forEach { (subjectName, sColor) ->
                    val subjectMockTests = mockTests.filter { it.subject == subjectName }
                    val mockCount = subjectMockTests.size
                    val avgScoreValue = if (mockCount > 0) subjectMockTests.map { it.score }.average() else 0.0

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurface)
                            .border(BorderStroke(1.dp, DarkBorder), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(sColor)
                            )
                            Column {
                                Text(
                                    text = subjectName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LightText
                                )
                                Text(
                                    text = "$mockCount Mock sessions recorded",
                                    fontSize = 10.sp,
                                    color = MutedText,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Text(
                            text = if (mockCount > 0) String.format("%.1f", avgScoreValue) else "—",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = sColor
                        )
                    }
                }
            }
        }
    }
}


// --- SETUP TAB ---
@Composable
fun SetupTab(
    viewModel: MockTrackerViewModel,
    activeMockId: Long?,
    mockTests: List<MockTestEntity>,
    onStartEntry: () -> Unit
) {
    val context = LocalContext.current
    val setupDate by viewModel.setupDate.collectAsStateWithLifecycle()
    val setupName by viewModel.setupName.collectAsStateWithLifecycle()
    val setupSubject by viewModel.setupSubject.collectAsStateWithLifecycle()
    val setupType by viewModel.setupType.collectAsStateWithLifecycle()
    val setupTotalQ by viewModel.setupTotalQ.collectAsStateWithLifecycle()
    val setupTotalTime by viewModel.setupTotalTime.collectAsStateWithLifecycle()

    var showHistoryScreen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mode Selector: New vs Load History
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showHistoryScreen = false },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!showHistoryScreen) AccentOrange else DarkSurface
                ),
                border = BorderStroke(1.dp, if (!showHistoryScreen) AccentOrange else DarkBorder),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    "Setup Mock Test",
                    fontWeight = FontWeight.Bold,
                    color = if (!showHistoryScreen) Color.White else MutedText
                )
            }

            Button(
                onClick = { showHistoryScreen = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (showHistoryScreen) AccentOrange else DarkSurface
                ),
                border = BorderStroke(1.dp, if (showHistoryScreen) AccentOrange else DarkBorder),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    "Session History (${mockTests.size})",
                    fontWeight = FontWeight.Bold,
                    color = if (showHistoryScreen) Color.White else MutedText
                )
            }
        }

        if (showHistoryScreen) {
            // SHOW PREVIOUS MOCKS
            if (mockTests.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Mock Sessions tracked yet.", color = MutedText, fontSize = 14.sp)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    mockTests.forEach { mock ->
                        val isSelected = activeMockId == mock.id
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) DarkCard else DarkSurface
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) AccentOrange else DarkBorder
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = mock.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = LightText
                                        )
                                        Text(
                                            text = "${mock.subject} • ${mock.type}",
                                            fontSize = 11.sp,
                                            color = MutedText,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Date: ${mock.date} • ${mock.totalQuestions} Questions",
                                            fontSize = 11.sp,
                                            color = MutedText
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = String.format("%.1f pts", mock.score),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 18.sp,
                                            color = AccentOrange
                                        )
                                        Text(
                                            text = "${mock.percentile.toInt()}% Accuracy",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StatusGreen
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.selectActiveMock(mock.id)
                                            Toast.makeText(context, "Session '${mock.name}' Loaded!", Toast.LENGTH_SHORT).show()
                                            onStartEntry()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) AccentOrange else DarkCard),
                                        border = if (isSelected) null else BorderStroke(1.dp, DarkBorder),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = if (isSelected) "Active (Open Logs)" else "Load Session",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else LightText
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            viewModel.deleteMock(mock)
                                            Toast.makeText(context, "Session Deleted", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(StatusRedDim)
                                            .size(40.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = StatusRed,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // SETUP FORM
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, DarkBorder),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Column {
                        Text("MOCK DATE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                        Spacer(modifier = Modifier.height(4.dp))
                        M3TextField(
                            value = setupDate,
                            onValueChange = { viewModel.setupDate.value = it },
                            placeholder = "YYYY-MM-DD",
                            keyboardType = KeyboardType.Text
                        )
                    }

                    Column {
                        Text("📌 MOCK TEST IDENTIFIER NAME / ID", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                        Spacer(modifier = Modifier.height(4.dp))
                        M3TextField(
                            value = setupName,
                            onValueChange = { viewModel.setupName.value = it },
                            placeholder = "e.g. PYST-21, TCS-05, General-01",
                            keyboardType = KeyboardType.Text
                        )
                    }

                    Column {
                        Text("📚 EXAM SUBJECT CATEGORY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                        Spacer(modifier = Modifier.height(4.dp))
                        DropdownSelector(
                            selected = setupSubject,
                            onSelectedChange = { viewModel.setupSubject.value = it },
                            options = listOf("Quantitative Aptitude", "Reasoning", "English", "General Awareness")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("🔢 TOTAL QUESTIONS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                            Spacer(modifier = Modifier.height(4.dp))
                            DoubleStepper(
                                value = setupTotalQ,
                                onValueChange = { viewModel.setupTotalQ.value = it },
                                min = 1,
                                max = 200,
                                label = "Qs"
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("⏱️ TARGET TIME", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                            Spacer(modifier = Modifier.height(4.dp))
                            DoubleStepper(
                                value = setupTotalTime,
                                onValueChange = { viewModel.setupTotalTime.value = it },
                                min = 5,
                                max = 240,
                                label = "Min"
                            )
                        }
                    }

                    Column {
                        Text("🗂️ MOCK TYPE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Sectional", "Full Mock").forEach { type ->
                                val selected = setupType == type
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (selected) AccentOrangeDim else DarkCard)
                                        .border(
                                            BorderStroke(
                                                1.dp,
                                                if (selected) AccentOrange else DarkBorder
                                            ), RoundedCornerShape(10.dp)
                                        )
                                        .clickable { viewModel.setupType.value = type }
                                        .padding(vertical = 12.dp, horizontal = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = type,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (selected) AccentOrange else LightText
                                    )
                                }
                            }
                        }
                    }

                    Divider(color = DarkBorder, modifier = Modifier.padding(vertical = 6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.saveMockSetup { _ ->
                                    Toast.makeText(context, "Mock Setup Ready!", Toast.LENGTH_SHORT).show()
                                    onStartEntry()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(2f)
                                .height(46.dp)
                        ) {
                            Text("Save & Start Logging", fontWeight = FontWeight.Black, color = Color.White)
                        }

                        Button(
                            onClick = {
                                viewModel.createNewMock()
                                Toast.makeText(context, "Cleared Form", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkCard),
                            border = BorderStroke(1.dp, DarkBorder),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        ) {
                            Text("Reset", fontWeight = FontWeight.Bold, color = LightText)
                        }
                    }
                }
            }
        }
    }
}

// --- ENTRY TAB (DETAILED logging screens) ---
@Composable
fun EntryTab(
    viewModel: MockTrackerViewModel,
    activeMockQuestions: List<QuestionEntity>,
    customReasonsDb: List<CustomReasonEntity>,
    customTagsDb: List<CustomTagEntity>,
    showFastBulkForm: Boolean,
    onToggleFastBulk: () -> Unit,
    fastBulkText: String,
    onFastBulkTextChange: (String) -> Unit,
    showCustomReasonEntry: Boolean,
    onToggleCustomReasonEntry: () -> Unit,
    customReasonText: String,
    onCustomReasonTextChange: (String) -> Unit,
    showCustomTagEntry: Boolean,
    onToggleCustomTagEntry: () -> Unit,
    customTagText: String,
    onCustomTagTextChange: (String) -> Unit,
    onGoToQuestions: () -> Unit
) {
    val context = LocalContext.current

    // Form collect
    val inputQNo by viewModel.inputQNo.collectAsStateWithLifecycle()
    val inputStatus by viewModel.inputStatus.collectAsStateWithLifecycle()
    val inputConfidence by viewModel.inputConfidence.collectAsStateWithLifecycle()
    val inputTimeTakenSeconds by viewModel.inputTimeTakenSeconds.collectAsStateWithLifecycle()
    val inputTimeDeltaSeconds by viewModel.inputTimeDeltaSeconds.collectAsStateWithLifecycle()
    val inputDifficultyLevel by viewModel.inputDifficultyLevel.collectAsStateWithLifecycle()
    val inputChapter by viewModel.inputChapter.collectAsStateWithLifecycle()
    val inputTopic by viewModel.inputTopic.collectAsStateWithLifecycle()
    val inputSubtopic by viewModel.inputSubtopic.collectAsStateWithLifecycle()
    val inputReason by viewModel.inputReason.collectAsStateWithLifecycle()
    val inputSelectedTags by viewModel.inputSelectedTags.collectAsStateWithLifecycle()
    val inputNotes by viewModel.inputNotes.collectAsStateWithLifecycle()
    val inputFormula by viewModel.inputFormula.collectAsStateWithLifecycle()
    val keepTopicOnNext by viewModel.keepTopicOnNext.collectAsStateWithLifecycle()
    val editingQuestionEntityId by viewModel.editingQuestionEntityId.collectAsStateWithLifecycle()
    val setupSubject by viewModel.setupSubject.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active Session Header Row
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, AccentOrange.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ACTIVE SESSION: ${viewModel.setupName.value}",
                        fontSize = 11.sp,
                        color = AccentOrange,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Logged ${activeMockQuestions.size} / ${viewModel.setupTotalQ.value} Questions",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = LightText
                    )
                }

                Button(
                    onClick = onGoToQuestions,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCard),
                    border = BorderStroke(1.dp, DarkBorder),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("View Records", fontSize = 11.sp, color = LightText)
                }
            }
        }

        // Fast Entry bulk panel toggle
        Card(
            colors = CardDefaults.cardColors(containerColor = if (showFastBulkForm) AccentOrangeDim.copy(alpha = 0.05f) else DarkSurface),
            border = BorderStroke(1.dp, if (showFastBulkForm) AccentOrange.copy(alpha = 0.3f) else DarkBorder),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("⚡", fontSize = 18.sp)
                        Column {
                            Text(
                                text = "FAST BULK TEXT ENTRY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = LightText
                            )
                            Text(
                                text = "Paste raw mock results to bulk save 50+ entries",
                                fontSize = 9.sp,
                                color = MutedText,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Button(
                        onClick = onToggleFastBulk,
                        colors = ButtonDefaults.buttonColors(containerColor = if (showFastBulkForm) AccentOrange else DarkCard),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (showFastBulkForm) "Close" else "Expand",
                            fontSize = 11.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (showFastBulkForm) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = fastBulkText,
                        onValueChange = onFastBulkTextChange,
                        placeholder = {
                            Text(
                                "Format example:\nQ=1 | S=C | Ch=Arithmetic | Topic=Percentage | Level=Easy | Time=45s | Reason=Perfect\n\nShort copy/paste positional:\n1 | C | Arithmetic > Percentage > Successive % | Easy | 45s | Silly Mistake",
                                fontSize = 11.sp,
                                color = MutedText
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkCard,
                            unfocusedContainerColor = DarkCard,
                            focusedBorderColor = AccentOrange,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = LightText,
                            unfocusedTextColor = LightText
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.saveFastBulk(
                                    bulkText = fastBulkText,
                                    onSuccess = { s, o ->
                                        Toast.makeText(context, "$s saved, $o overwritten!", Toast.LENGTH_LONG).show()
                                        onToggleFastBulk()
                                        onFastBulkTextChange("")
                                    },
                                    onError = { err ->
                                        Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Convert & Import Batch", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                onFastBulkTextChange(
                                    "Q=1 | S=C | Ch=Arithmetic | Topic=Percentage | Level=Easy | Time=45s | Reason=Perfect\n" +
                                    "Q=2 | S=I | Ch=Arithmetic | Topic=Ratio & Proportion | Level=Hard | Time=110s | Reason=Calculation Error\n" +
                                    "Q=3 | S=U | Ch=Algebra | Topic=Quadratic Equations | Level=Medium | Time=12s | Reason=Revision Needed"
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkCard),
                            border = BorderStroke(1.dp, DarkBorder),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(0.6f)
                        ) {
                            Text("Demo load", color = LightText, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // ENTRY FORM CORE
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, DarkBorder),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Q. Number Stepper Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (editingQuestionEntityId != null) "EDITING QUESTION NUMBER" else "QUESTION NUMBER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentOrange
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DoubleStepper(
                                value = inputQNo,
                                onValueChange = { viewModel.inputQNo.value = it },
                                min = 1,
                                max = 200,
                                label = "Q#"
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(StatusGreenDim)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Accuracy: ${if (activeMockQuestions.isNotEmpty()) (activeMockQuestions.count { it.status == "Correct" } * 100 / activeMockQuestions.size) else 0}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusGreen
                        )
                    }
                }

                // Status selection Correct, Incorrect, Unattempted
                Column {
                    Text("STATUS / ACCURACY OUTCOME", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("Correct", "✅ Correct", StatusGreen),
                            Triple("Incorrect", "❌ Wrong", StatusRed),
                            Triple("Unattempted", "⬜ Skipped", StatusAmber)
                        ).forEach { (statusVal, sLabel, sColor) ->
                            val isSelected = inputStatus == statusVal
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) sColor.copy(alpha = 0.15f) else DarkCard)
                                    .border(
                                        BorderStroke(
                                            2.dp,
                                            if (isSelected) sColor else DarkBorder
                                        ), RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.inputStatus.value = statusVal }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = sLabel,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isSelected) sColor else LightText
                                )
                            }
                        }
                    }
                }

                // Confidence Slider Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.3f)) {
                        Text("💪 CONFIDENCE LEVEL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("High", "Medium", "Low").forEach { conf ->
                                val corr = inputConfidence == conf
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (corr) DarkBorder else DarkCard)
                                        .border(BorderStroke(1.dp, DarkBorder), RoundedCornerShape(8.dp))
                                        .clickable { viewModel.inputConfidence.value = conf }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = conf,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (corr) AccentOrange else LightText
                                    )
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("⏱️ TIME SPENT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                        Spacer(modifier = Modifier.height(4.dp))
                        DoubleStepper(
                            value = inputTimeTakenSeconds,
                            onValueChange = { viewModel.inputTimeTakenSeconds.value = it },
                            min = 0,
                            max = 999,
                            label = "sec"
                        )
                    }
                }

                // Overtime / Time Delta Selection
                Column {
                    Text("⏱️ TIME DELTA (OVERTIME / TIME SAVED)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.inputTimeDeltaSeconds.value -= 10 },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusGreenDim),
                            border = BorderStroke(1.dp, StatusGreen.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(42.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("-10s", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                        }

                        OutlinedTextField(
                            value = if (inputTimeDeltaSeconds == 0) "" else inputTimeDeltaSeconds.toString(),
                            onValueChange = { viewModel.inputTimeDeltaSeconds.value = it.toIntOrNull() ?: 0 },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            placeholder = { Text("Overtime: +s / Saved: -s", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                  focusedContainerColor = DarkCard,
                                  unfocusedContainerColor = DarkCard,
                                  focusedBorderColor = AccentOrange,
                                  unfocusedBorderColor = DarkBorder,
                                  focusedTextColor = LightText,
                                  unfocusedTextColor = LightText
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )

                        Button(
                            onClick = { viewModel.inputTimeDeltaSeconds.value += 10 },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusRedDim),
                            border = BorderStroke(1.dp, StatusRed.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(42.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("+10s", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusRed)
                        }
                    }
                }

                // Difficulty Level Selector
                Column {
                    Text("👥 DIFFICULTY / PERCENTAGE OF CANDIDATES RIGHT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                    Spacer(modifier = Modifier.height(4.dp))
                    DropdownSelector(
                        selected = inputDifficultyLevel,
                        onSelectedChange = { viewModel.inputDifficultyLevel.value = it },
                        options = listOf("Very Easy", "Easy", "Medium", "Hard", "Very Hard")
                    )
                }

                // Auto-completion/hierarchy based Classification for Chapter, Topic, Subtopic List
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "TOPIC CLASSIFICATIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentOrange,
                        letterSpacing = 0.5.sp
                    )

                    Column {
                        Text("📂 Primary Chapter", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedText)
                        Spacer(modifier = Modifier.height(2.dp))

                        val topLevelChapters = TopicHierarchy.hierarchy[setupSubject]?.keys?.toList() ?: emptyList()
                        DropdownSelector(
                            selected = inputChapter,
                            onSelectedChange = { viewModel.inputChapter.value = it },
                            options = topLevelChapters
                        )
                    }

                    Column {
                        Text("📌 Topic Detail", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedText)
                        Spacer(modifier = Modifier.height(2.dp))

                        val topicsForChapter = TopicHierarchy.hierarchy[setupSubject]?.get(inputChapter) ?: emptyList()
                        DropdownSelector(
                            selected = inputTopic,
                            onSelectedChange = { viewModel.inputTopic.value = it },
                            options = topicsForChapter
                        )
                    }

                    Column {
                        Text("🔖 Subtopic Scope", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedText)
                        Spacer(modifier = Modifier.height(2.dp))
                        M3TextField(
                            value = inputSubtopic,
                            onValueChange = { viewModel.inputSubtopic.value = it },
                            placeholder = "e.g. Successive % increase, Replacement weight",
                            keyboardType = KeyboardType.Text
                        )
                    }
                }

                // Mistake Reasons
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📌 MISTAKE REASON / OBSERVATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                        Text(
                            text = "+ Add Custom",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentOrange,
                            modifier = Modifier.clickable { onToggleCustomReasonEntry() }
                        )
                    }

                    if (showCustomReasonEntry) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = customReasonText,
                                onValueChange = onCustomReasonTextChange,
                                placeholder = { Text("Add custom reason...", fontSize = 12.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = DarkCard,
                                    unfocusedContainerColor = DarkCard,
                                    focusedTextColor = LightText,
                                    unfocusedTextColor = LightText,
                                    focusedBorderColor = AccentOrange,
                                    unfocusedBorderColor = DarkBorder
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    if (customReasonText.isNotBlank()) {
                                        viewModel.addCustomReason(customReasonText)
                                        viewModel.inputReason.value = customReasonText.trim()
                                        onCustomReasonTextChange("")
                                        onToggleCustomReasonEntry()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                                modifier = Modifier.height(44.dp)
                            ) {
                                Text("Add", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Reason grid
                    val allLocalReasons = TopicHierarchy.defaultReasons.map { it.first } + customReasonsDb.map { it.reason }
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(vertical = 2.dp)
                    ) {
                        items(allLocalReasons) { reason ->
                            val selected = inputReason == reason
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selected) AccentOrangeDim else DarkCard)
                                    .border(
                                        BorderStroke(
                                            1.dp,
                                            if (selected) AccentOrange else DarkBorder
                                        ), RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        viewModel.inputReason.value = if (selected) "" else reason
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                val em = TopicHierarchy.defaultReasons.find { it.first == reason }?.second ?: "📌"
                                Text(
                                    text = "$em $reason",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) AccentOrange else LightText
                                )
                            }
                        }
                    }
                }

                // Tags Layout Configuration
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🏷️ SPECIAL TAGS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                        Text(
                            text = "+ Create Tag",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentOrange,
                            modifier = Modifier.clickable { onToggleCustomTagEntry() }
                        )
                    }

                    if (showCustomTagEntry) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = customTagText,
                                onValueChange = onCustomTagTextChange,
                                placeholder = { Text("Name custom tag...", fontSize = 12.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = DarkCard,
                                    unfocusedContainerColor = DarkCard,
                                    focusedTextColor = LightText,
                                    unfocusedTextColor = LightText,
                                    focusedBorderColor = AccentOrange,
                                    unfocusedBorderColor = DarkBorder
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    if (customTagText.isNotBlank()) {
                                        viewModel.addCustomTag(customTagText)
                                        viewModel.toggleTagSelection(customTagText.trim())
                                        onCustomTagTextChange("")
                                        onToggleCustomTagEntry()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                                modifier = Modifier.height(44.dp)
                            ) {
                                Text("Add", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val allLocalTags = TopicHierarchy.defaultTags + customTagsDb.map { it.tag }
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        allLocalTags.forEach { tag ->
                            val selected = tag in inputSelectedTags
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (selected) AccentOrange else DarkCard)
                                    .border(BorderStroke(1.dp, DarkBorder), RoundedCornerShape(16.dp))
                                    .clickable { viewModel.toggleTagSelection(tag) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) Color.White else LightText
                                )
                            }
                        }
                    }
                }

                // Formulas and Extra Notes Text Areas
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column {
                        Text("📝 EXTRA NOTES / SHORTCUT WORKINGS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                        Spacer(modifier = Modifier.height(4.dp))
                        M3TextField(
                            value = inputNotes,
                            onValueChange = { viewModel.inputNotes.value = it },
                            placeholder = "Summarize trick or mistakes patterns here...",
                            keyboardType = KeyboardType.Text,
                            singleLine = false,
                            maxLines = 3
                        )
                    }

                    Column {
                        Text("📐 FORMULA BOOKMARK / QUICK TRICKS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
                        Spacer(modifier = Modifier.height(4.dp))
                        M3TextField(
                            value = inputFormula,
                            onValueChange = { viewModel.inputFormula.value = it },
                            placeholder = "e.g. Area = 1/2 * b * h, Profit = (Dis% - Mark%)",
                            keyboardType = KeyboardType.Text,
                            singleLine = false,
                            maxLines = 2
                        )
                    }
                }

                // Lock topic state representation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = keepTopicOnNext,
                        onCheckedChange = { viewModel.keepTopicOnNext.value = it },
                        colors = CheckboxDefaults.colors(checkedColor = AccentOrange)
                    )
                    Text(
                        "Lock classification details for next question",
                        fontSize = 12.sp,
                        color = MutedText,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Divider(color = DarkBorder, modifier = Modifier.padding(vertical = 4.dp))

                // SAVE ACTION TRIGGERS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.saveQuestion {
                                Toast.makeText(context, "Saved Question Q${inputQNo}!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(2f)
                            .height(48.dp)
                    ) {
                        Text(
                            text = if (editingQuestionEntityId != null) "Update Question Q${inputQNo}" else "Save & Next Q# ${inputQNo + 1}",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.clearQuestionForm()
                            Toast.makeText(context, "Entry Form Cleared", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkCard),
                        border = BorderStroke(1.dp, DarkBorder),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Clear", color = LightText, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        content = { content() }
    )
}

// Custom Reusable Display Components
@Composable
fun M3TextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    singleLine: Boolean = true,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, fontSize = 13.sp, color = MutedText) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = DarkCard,
            unfocusedContainerColor = DarkCard,
            focusedTextColor = LightText,
            unfocusedTextColor = LightText,
            focusedBorderColor = AccentOrange,
            unfocusedBorderColor = DarkBorder
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        maxLines = maxLines,
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
    )
}

@Composable
fun DoubleStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    min: Int,
    max: Int,
    label: String
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .border(BorderStroke(1.5.dp, DarkBorder), RoundedCornerShape(12.dp))
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { if (value > min) onValueChange(value - 1) },
            modifier = Modifier.width(44.dp)
        ) {
            Text("-", color = LightText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Text(
            text = "$value $label",
            color = LightText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        IconButton(
            onClick = { if (value < max) onValueChange(value + 1) },
            modifier = Modifier.width(44.dp)
        ) {
            Text("+", color = LightText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DropdownSelector(
    selected: String,
    onSelectedChange: (String) -> Unit,
    options: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkCard)
                .border(BorderStroke(1.5.dp, DarkBorder), RoundedCornerShape(12.dp))
                .clickable { expanded = !expanded }
                .padding(horizontal = 14.dp, vertical = 13.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selected.ifBlank { "Select option" },
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected.isNotBlank()) LightText else MutedText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Dropdown",
                tint = MutedText
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(DarkSurface)
                .border(BorderStroke(1.dp, DarkBorder))
                .fillMaxWidth(0.9f)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            fontWeight = FontWeight.SemiBold,
                            color = LightText,
                            fontSize = 13.sp
                        )
                    },
                    onClick = {
                        onSelectedChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


// --- MISTAKE LOG (QUESTIONS) TAB ---
@Composable
fun QuestionsTab(
    viewModel: MockTrackerViewModel,
    questions: List<QuestionEntity>,
    mockTests: List<MockTestEntity>
) {
    val context = LocalContext.current
    val searchQuery by viewModel.questionSearchQuery.collectAsStateWithLifecycle()
    val statusFilter by viewModel.questionStatusFilter.collectAsStateWithLifecycle()
    val difficultyFilter by viewModel.questionDifficultyFilter.collectAsStateWithLifecycle()
    val chapterFilter by viewModel.questionChapterFilter.collectAsStateWithLifecycle()

    // Loaded mock test mapped ID reference helper
    val mockMap = remember(mockTests) { mockTests.associateBy { it.id } }

    val filteredList = remember(questions, searchQuery, statusFilter, difficultyFilter, chapterFilter) {
        questions.filter { q ->
            val matchesSearch = searchQuery.isBlank() ||
                    q.chapter.contains(searchQuery, true) ||
                    q.topic.contains(searchQuery, true) ||
                    q.subtopic.contains(searchQuery, true) ||
                    q.notes.contains(searchQuery, true) ||
                    q.formula.contains(searchQuery, true) ||
                    q.tags.contains(searchQuery, true)

            val matchesStatus = statusFilter == "All" || q.status == statusFilter
            val matchesDifficulty = difficultyFilter == "All" || q.difficultyLevel == difficultyFilter
            val matchesChapter = chapterFilter == "All" || q.chapter == chapterFilter

            matchesSearch && matchesStatus && matchesDifficulty && matchesChapter
        }.reversed() // Show newest first
    }

    val uniqueChapters = remember(questions) {
        listOf("All") + questions.map { it.chapter }.filter { it.isNotBlank() }.distinct().sorted()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Search & Filter Panel
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.questionSearchQuery.value = it },
            placeholder = { Text("Search topics, notes, formulas...", fontSize = 13.sp, color = MutedText) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = MutedText) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface,
                focusedTextColor = LightText,
                unfocusedTextColor = LightText,
                focusedBorderColor = AccentOrange,
                unfocusedBorderColor = DarkBorder
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        // Dropdowns for advanced filters in rows
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                DropdownSelector(
                    selected = statusFilter,
                    onSelectedChange = { viewModel.questionStatusFilter.value = it },
                    options = listOf("All", "Correct", "Incorrect", "Unattempted")
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                DropdownSelector(
                    selected = difficultyFilter,
                    onSelectedChange = { viewModel.questionDifficultyFilter.value = it },
                    options = listOf("All", "Very Easy", "Easy", "Medium", "Hard", "Very Hard")
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Chapter:", fontSize = 11.sp, color = MutedText, fontWeight = FontWeight.Bold)
            Box(modifier = Modifier.weight(1f)) {
                DropdownSelector(
                    selected = chapterFilter,
                    onSelectedChange = { viewModel.questionChapterFilter.value = it },
                    options = uniqueChapters
                )
            }
        }

        // Active listing
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No matching question records found.", color = MutedText, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList) { question ->
                    val mockName = mockMap[question.mockId]?.name ?: "Mock Session"

                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, if (question.status == "Correct") StatusGreen.copy(alpha = 0.3f) else if (question.status == "Incorrect") StatusRed.copy(alpha = 0.3f) else DarkBorder),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Title row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "$mockName • Q${question.qNo}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = AccentOrange
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (question.status == "Correct") StatusGreenDim else if (question.status == "Incorrect") StatusRedDim else StatusAmberDim
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = question.status,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (question.status == "Correct") StatusGreen else if (question.status == "Incorrect") StatusRed else StatusAmber
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = {
                                            viewModel.startEditingQuestion(question)
                                            Toast.makeText(context, "Editing load in Logs tab!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = StatusBlue, modifier = Modifier.size(16.dp))
                                    }

                                    IconButton(
                                        onClick = {
                                            viewModel.deleteQuestion(question)
                                            Toast.makeText(context, "Record Deleted", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = StatusRed, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            // Classifications details
                            Text(
                                text = "${question.chapter}  →  ${question.topic}  →  ${question.subtopic}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LightText
                            )

                            // Additional info
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                miniStat("Time Taken", "${question.timeTakenSeconds}s")
                                if (question.timeDeltaSeconds != 0) {
                                    val deltaLabel = if (question.timeDeltaSeconds > 0) "+${question.timeDeltaSeconds}s" else "${question.timeDeltaSeconds}s"
                                    miniStat("Time Delta", deltaLabel, color = if (question.timeDeltaSeconds > 0) StatusRed else StatusGreen)
                                }
                                miniStat("Difficulty", question.difficultyLevel)
                                if (question.reason.isNotBlank()) {
                                    miniStat("Observation/Error", question.reason, color = StatusAmber)
                                }
                            }

                            if (question.tags.isNotBlank()) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    question.tags.split(",").forEach { tag ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(DarkBorder)
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(tag, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = LightText)
                                        }
                                    }
                                }
                            }

                            if (question.notes.isNotBlank()) {
                                Text(
                                    text = "Notes: ${question.notes}",
                                    fontSize = 11.sp,
                                    color = MutedText,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (question.formula.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(StatusBlueDim)
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "📐 ${question.formula}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusBlue,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


// --- ANALYTICS TAB ---
@Composable
fun AnalyticsTab(
    questions: List<QuestionEntity>,
    mockTests: List<MockTestEntity>,
    viewModel: MockTrackerViewModel
) {
    val selectedSubject by viewModel.analyticsSubjectFilter.collectAsStateWithLifecycle()
    val selectedType by viewModel.analyticsTypeFilter.collectAsStateWithLifecycle()

    val displayMockTests = remember(mockTests, selectedSubject, selectedType) {
        mockTests.filter { mock ->
            (selectedSubject == "All" || mock.subject == selectedSubject) &&
                    (selectedType == "All" || mock.type == selectedType)
        }
    }

    val displayMockIds = remember(displayMockTests) { displayMockTests.map { it.id }.toSet() }

    val displayQuestions = remember(questions, displayMockIds) {
        questions.filter { it.mockId in displayMockIds }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Filters Row
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, DarkBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("DASHBOARD FILTERING ENGINE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedText)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.weight(1.3f)) {
                        DropdownSelector(
                            selected = selectedSubject,
                            onSelectedChange = { viewModel.analyticsSubjectFilter.value = it },
                            options = listOf("All", "Quantitative Aptitude", "Reasoning", "English", "General Awareness")
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        DropdownSelector(
                            selected = selectedType,
                            onSelectedChange = { viewModel.analyticsTypeFilter.value = it },
                            options = listOf("All", "Sectional", "Full Mock")
                        )
                    }
                }
            }
        }

        if (displayQuestions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No analytics datasets matching selection.", color = MutedText, fontSize = 13.sp)
            }
        } else {
            val correct = displayQuestions.count { it.status == "Correct" }
            val wrong = displayQuestions.count { it.status == "Incorrect" }
            val total = displayQuestions.size
            val accuracy = if (correct + wrong > 0) (correct.toDouble() / (correct + wrong).toDouble()) * 100.0 else 0.0

            // Custom metrics
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricCard(
                    title = "Selected Accuracy",
                    value = "${accuracy.toInt()}%",
                    color = StatusGreen,
                    label = "Attempts Accuracy",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Avg Answer Time",
                    value = "${if (total > 0) displayQuestions.map { it.timeTakenSeconds }.average().toInt() else 0}s",
                    color = StatusAmber,
                    label = "Timer Efficiency",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Total Time Delta",
                    value = "${displayQuestions.sumOf { it.timeDeltaSeconds }}s",
                    color = StatusRed,
                    label = "Accumulated Overrun",
                    modifier = Modifier.weight(1f)
                )
            }

            // Custom Doughnut Arc Chart using Compose Canvas
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, DarkBorder),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "ERROR TYPE MIGRATIONS DEVIATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedText,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    val mistakeReasons = displayQuestions.filter { it.status == "Incorrect" && it.reason.isNotBlank() }
                        .groupBy { it.reason }
                        .mapValues { it.value.size }
                        .toList()
                        .sortedByDescending { it.second }

                    if (mistakeReasons.isEmpty()) {
                        Text("No mistake observations logged for analysis.", color = MutedText, fontSize = 12.sp, modifier = Modifier.padding(vertical = 12.dp))
                    } else {
                        // Plot canvas
                        Box(
                            modifier = Modifier
                                .size(130.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(110.dp)) {
                                var startAngle = -90f
                                val totalMistakes = mistakeReasons.sumOf { it.second }.toFloat()
                                val mColors = listOf(StatusRed, StatusAmber, StatusBlue, Color(0xFF9C27B0), AccentOrange)

                                mistakeReasons.forEachIndexed { index, pair ->
                                    val sweep = (pair.second.toFloat() / totalMistakes) * 360f
                                    drawArc(
                                        color = mColors[index % mColors.size],
                                        startAngle = startAngle,
                                        sweepAngle = sweep,
                                        useCenter = false,
                                        style = Stroke(width = 16.dp.toPx())
                                    )
                                    startAngle += sweep
                                }
                            }
                            Text(
                                text = "Mistakes",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = LightText
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Chart legend
                        val mColors = listOf(StatusRed, StatusAmber, StatusBlue, Color(0xFF9C27B0), AccentOrange)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            mistakeReasons.take(4).forEachIndexed { index, pair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(mColors[index % mColors.size])
                                        )
                                        Text(pair.first, fontSize = 12.sp, color = LightText, fontWeight = FontWeight.SemiBold)
                                    }
                                    Text("${pair.second} occurrences", fontSize = 11.sp, color = MutedText, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Chapter performance metrics table mockup details
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, DarkBorder),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        "TOPIC ACCURACY GRID LOGS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedText,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val chapterGroups = displayQuestions.filter { it.chapter.isNotBlank() }.groupBy { it.chapter }
                    chapterGroups.forEach { (chapter, qList) ->
                        val chCorrect = qList.count { it.status == "Correct" }
                        val chTotal = qList.size
                        val chAccuracy = (chCorrect.toDouble() / chTotal.toDouble()) * 100.0

                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(chapter, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LightText)
                                Text("${chAccuracy.toInt()}% Acc ($chTotal Qs)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (chAccuracy >= 80.0) StatusGreen else if (chAccuracy >= 50.0) StatusAmber else StatusRed)
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            // Simple performance bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(DarkBorder)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(chAccuracy.toFloat() / 100f)
                                        .height(5.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(
                                            if (chAccuracy >= 80) StatusGreen else if (chAccuracy >= 50) StatusAmber else StatusRed
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


// --- REVISION TAB ---
@Composable
fun RevisionTab(
    questions: List<QuestionEntity>,
    mockTests: List<MockTestEntity>,
    viewModel: MockTrackerViewModel
) {
    var notebookMode by remember { mutableStateOf("Mistakes") } // Mistakes, Formulas, Strategies

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Folder Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Mistakes", "Formulas", "Strategies").forEach { mode ->
                Button(
                    onClick = { notebookMode = mode },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (notebookMode == mode) AccentOrange else DarkSurface
                    ),
                    border = BorderStroke(1.dp, if (notebookMode == mode) AccentOrange else DarkBorder),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Text(
                        text = if (mode == "Mistakes") "📕 Mistakes" else if (mode == "Formulas") "📐 Formulas" else "🚀 Custom Plan",
                        fontWeight = FontWeight.Bold,
                        color = if (notebookMode == mode) Color.White else MutedText,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Listing Box
        Box(modifier = Modifier.weight(1f)) {
            when (notebookMode) {
                "Mistakes" -> {
                    val mistakeList = questions.filter { it.status == "Incorrect" }
                    if (mistakeList.isEmpty()) {
                        EmptyState("No recorded mistakes. High efficiency achieved!")
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(mistakeList) { q ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                    border = BorderStroke(1.dp, StatusRed.copy(alpha = 0.4f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                "Q${q.qNo} • ${q.chapter}",
                                                fontWeight = FontWeight.Bold,
                                                color = AccentOrange,
                                                fontSize = 13.sp
                                            )
                                            if (q.reason.isNotBlank()) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(StatusRedDim)
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(q.reason, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = StatusRed)
                                                }
                                            }
                                        }

                                        Text(
                                            text = "Topic: ${q.topic} [${q.subtopic}]",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = LightText
                                        )

                                        if (q.notes.isNotBlank()) {
                                            Text(
                                                text = "Observation Strategy: ${q.notes}",
                                                fontSize = 11.sp,
                                                color = MutedText
                                            )
                                        }

                                        if (q.formula.isNotBlank()) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(StatusBlueDim)
                                                    .padding(6.dp)
                                            ) {
                                                Text(
                                                    text = "📐 Formula: ${q.formula}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = StatusBlue
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                "Formulas" -> {
                    val formulaList = questions.filter { it.formula.isNotBlank() }
                    if (formulaList.isEmpty()) {
                        EmptyState("No bookmarked formula bookmarks registered.")
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(formulaList) { q ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                    border = BorderStroke(1.dp, StatusBlue.copy(alpha = 0.4f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = "${q.chapter}  →  ${q.topic}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MutedText
                                        )

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(StatusBlueDim)
                                                .padding(8.dp)
                                        ) {
                                            Text(
                                                text = q.formula,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = StatusBlue,
                                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                            )
                                        }

                                        if (q.notes.isNotBlank()) {
                                            Text(text = "Shortcut application: ${q.notes}", fontSize = 11.sp, color = LightText)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                "Strategies" -> {
                    // Auto strategizer
                    val weakChaptersList = remember(questions) {
                        questions.filter { it.chapter.isNotBlank() }
                            .groupBy { it.chapter }
                            .mapValues { entry ->
                                val chCorrect = entry.value.count { it.status == "Correct" }
                                val chTotal = entry.value.size
                                val chAccuracy = (chCorrect.toDouble() / chTotal.toDouble()) * 100.0
                                Pair(chTotal, chAccuracy)
                            }
                            .toList()
                            .filter { it.second.first >= 2 && it.second.second < 65.0 }
                            .sortedBy { it.second.second }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Title header
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AccentOrangeDim.copy(alpha = 0.08f)),
                            border = BorderStroke(1.dp, AccentOrange.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("🚀 ACCELERATED STUDY ROADMAP GENERATOR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentOrange)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("We analyze mock history patterns dynamically to output your weekly practice priorities on-the-fly.", fontSize = 11.sp, color = MutedText)
                            }
                        }

                        if (weakChaptersList.isEmpty()) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = StatusGreenDim),
                                border = BorderStroke(1.dp, StatusGreen.copy(alpha = 0.3f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("🎯 Target Peak Efficiency Achieved!", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("All your subjects hold above 65%+ logs accuracy. Continue taking full mocks periodically to hold peak response threshold.", fontSize = 12.sp, color = LightText)
                                }
                            }
                        } else {
                            Text(
                                "HIGH PRIORITY TOPICS PRACTICE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MutedText,
                                letterSpacing = 0.5.sp
                            )

                            weakChaptersList.forEachIndexed { idx, pair ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                    border = BorderStroke(1.dp, DarkBorder),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Priority ${idx + 1}: ${pair.first}",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = LightText
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(StatusRedDim)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "Accuracy ${pair.second.second.toInt()}%",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = StatusRed
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "💡 Study Advice: Solve at least 25 targeted PYQ questions for ${pair.first}. Create a compact note summarizing the core concept failures and bookmark your formula shortcuts.",
                                            fontSize = 11.sp,
                                            color = MutedText,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(msg: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🗂️", fontSize = 36.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                msg,
                fontSize = 13.sp,
                color = MutedText,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// --- HELPER METRIC & MATH UTILS ---
@Composable
fun MetricCard(
    title: String,
    value: String,
    color: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, DarkBorder),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedText)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(label, fontSize = 9.sp, color = MutedText, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun miniStat(title: String, value: String, color: Color = LightText) {
    Column {
        Text(title, fontSize = 9.sp, color = MutedText, fontWeight = FontWeight.Bold)
        Text(value, fontSize = 11.sp, color = color, fontWeight = FontWeight.SemiBold)
    }
}

fun isSillyMistake(reason: String): Boolean {
    return reason.contains("Silly Mistake", ignoreCase = true) || 
           reason.contains("Calculation Error", ignoreCase = true) ||
           reason.contains("Calculative", ignoreCase = true)
}

fun ringProgressHelper(accuracy: Double): Double {
    return (accuracy / 100.0).coerceIn(0.0, 1.0)
}

fun calculateStreak(mockTests: List<MockTestEntity>): Int {
    if (mockTests.isEmpty()) return 0
    val dates = mockTests.map { it.date }.distinct().sorted()
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
    val today = sdf.format(java.util.Date())
    var streak = 0
    val activeDates = mutableListOf<String>()
    
    // Simple mock logic for demonstration
    // If we have tests, determine streak based on continuous date logging
    var checkDate = today
    val cal = java.util.Calendar.getInstance()
    while (true) {
        if (dates.contains(checkDate)) {
            streak++
            cal.time = sdf.parse(checkDate) ?: java.util.Date()
            cal.add(java.util.Calendar.DATE, -1)
            checkDate = sdf.format(cal.time)
        } else {
            // Check if user has logged yesterday if not today
            if (checkDate == today) {
                cal.time = java.util.Date()
                cal.add(java.util.Calendar.DATE, -1)
                val checkYesterday = sdf.format(cal.time)
                if (checkYesterday in dates) {
                    checkDate = checkYesterday
                    continue
                }
            }
            break
        }
    }
    return streak.coerceAtLeast(mockTests.size.coerceAtMost(1)) // fallback to at least 1 if tests exist
}

data class WeakestTopicResult(val chapter: String, val topic: String, val total: Int, val accuracy: Double)

fun findWeakestTopic(questions: List<QuestionEntity>): WeakestTopicResult? {
    if (questions.isEmpty()) return null
    val groups = questions.filter { it.chapter.isNotBlank() }
        .groupBy { it.chapter + "||" + it.topic }

    val computed = groups.map { (key, list) ->
        val parts = key.split("||")
        val correct = list.count { it.status == "Correct" }
        val total = list.size
        val accuracy = (correct.toDouble() / total.toDouble()) * 100.0
        WeakestTopicResult(parts.getOrNull(0) ?: "", parts.getOrNull(1) ?: "", total, accuracy)
    }

    // Return the weakest topic that has at least 1 attempt and less than 70% accuracy
    return computed.filter { it.total >= 1 && it.accuracy < 70.0 }
        .minByOrNull { it.accuracy }
}

fun getReadinessTip(accuracy: Double, avgScore: Double, totalMocks: Int): String {
    if (totalMocks == 0) return "Setup your first mock session to start!"
    return when {
        accuracy >= 85.0 && avgScore >= 42.0 -> "🔥 Target Tier-1 Achieved! Excellent Speed & Accuracy."
        accuracy >= 70.0 && avgScore >= 35.0 -> "📈 On track. Reduce Silly Mistakes in Quant to hit 40+."
        accuracy >= 50.0 -> "⚠️ Focus on conceptual reviews and speed elimination drills."
        else -> "🚨 High error margin detected. Leverage formula notebooks before re-mocking."
    }
}
