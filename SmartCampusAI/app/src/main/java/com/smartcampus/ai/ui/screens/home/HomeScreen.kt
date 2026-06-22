package com.smartcampus.ai.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcampus.ai.domain.model.*
import com.smartcampus.ai.ui.components.*
import com.smartcampus.ai.ui.navigation.Screen
import com.smartcampus.ai.ui.theme.SmartCampusColors
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateTo: (String) -> Unit,
    onPomodoroClick: () -> Unit,
    onAttendanceClick: () -> Unit,
    onTimetableClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val pendingTasks by viewModel.pendingTasksCount.collectAsStateWithLifecycle()
    val completedTasks by viewModel.completedTasksCount.collectAsStateWithLifecycle()
    val focusMinutes by viewModel.totalFocusMinutes.collectAsStateWithLifecycle()
    val upcomingAssignments by viewModel.upcomingAssignments.collectAsStateWithLifecycle()
    val todayClasses by viewModel.todayClasses.collectAsStateWithLifecycle()
    val motivationalQuote by viewModel.motivationalQuote.collectAsStateWithLifecycle()
    val overallAttendance by viewModel.overallAttendance.collectAsStateWithLifecycle()
    val shortageSubjects by viewModel.shortageSubjects.collectAsStateWithLifecycle()

    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..20 -> "Good evening"
        else -> "Good night"
    }
    val today = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault()).format(Date())

    Scaffold(
        containerColor = SmartCampusColors.Background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // ── Header ──────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    SmartCampusColors.Surface,
                                    SmartCampusColors.Background
                                )
                            )
                        )
                ) {
                    Canvas(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                        drawCircle(
                            color = SmartCampusColors.Primary.copy(alpha = 0.08f),
                            radius = 200.dp.toPx(),
                            center = Offset(size.width, 0f)
                        )
                    }
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "$greeting,",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = SmartCampusColors.OnSurfaceVariant
                                )
                                Text(
                                    text = userName.ifEmpty { "Student" } + " 👋",
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = today,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SmartCampusColors.OnSurfaceVariant
                                )
                            }
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(SmartCampusColors.Primary, SmartCampusColors.Secondary)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userName.firstOrNull()?.uppercase() ?: "S",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // ── Stats Row ───────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatsCard(
                        title = "Pending",
                        value = "$pendingTasks",
                        icon = Icons.Default.Assignment,
                        color = SmartCampusColors.PriorityHigh,
                        modifier = Modifier.weight(1f),
                        subtitle = "tasks"
                    )
                    StatsCard(
                        title = "Done",
                        value = "$completedTasks",
                        icon = Icons.Default.CheckCircle,
                        color = SmartCampusColors.PriorityLow,
                        modifier = Modifier.weight(1f),
                        subtitle = "tasks"
                    )
                    StatsCard(
                        title = "Focus",
                        value = if (focusMinutes >= 60) "${focusMinutes / 60}h" else "${focusMinutes}m",
                        icon = Icons.Default.Timer,
                        color = SmartCampusColors.Primary,
                        modifier = Modifier.weight(1f),
                        subtitle = "total"
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            // ── Quick Actions ───────────────────
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SectionHeader(title = "Quick Actions")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(quickActions(
                            onTasksClick = { onNavigateTo(Screen.Tasks.route) },
                            onNotesClick = { onNavigateTo(Screen.Notes.route) },
                            onPomodoroClick = onPomodoroClick,
                            onAttendanceClick = onAttendanceClick,
                            onTimetableClick = onTimetableClick,
                            onAIClick = { onNavigateTo(Screen.AiAssistant.route) }
                        )) { action ->
                            QuickActionCard(action = action)
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            // ── Motivational Quote ──────────────
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        SmartCampusColors.Primary.copy(alpha = 0.3f),
                                        SmartCampusColors.Secondary.copy(alpha = 0.2f)
                                    )
                                ),
                                RoundedCornerShape(16.dp)
                            )
                            .border(
                                1.dp,
                                SmartCampusColors.Primary.copy(alpha = 0.3f),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        null,
                                        tint = SmartCampusColors.Tertiary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        "Daily Inspiration",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = SmartCampusColors.Tertiary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.refreshQuote() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        null,
                                        tint = SmartCampusColors.OnSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text(
                                text = motivationalQuote,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            // ── Attendance Alert ─────────────────
            if (shortageSubjects.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                null,
                                tint = SmartCampusColors.Error,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                "Attendance Shortage",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = SmartCampusColors.Error
                            )
                        }
                        shortageSubjects.take(2).forEach { attendance ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        SmartCampusColors.Error.copy(alpha = 0.1f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .border(
                                        1.dp,
                                        SmartCampusColors.Error.copy(alpha = 0.3f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .padding(12.dp)
                                    .clickable { onAttendanceClick() }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        attendance.subject,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        "${attendance.percentage.toInt()}%",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = SmartCampusColors.Error,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }
            }

            // ── Today's Schedule ─────────────────
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SectionHeader(
                        title = "Today's Classes",
                        actionLabel = "Full Timetable",
                        onAction = onTimetableClick
                    )
                    if (todayClasses.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    SmartCampusColors.SurfaceVariant,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Weekend, null, tint = SmartCampusColors.OnSurfaceVariant, modifier = Modifier.size(32.dp))
                                Spacer(Modifier.height(8.dp))
                                Text("No classes today 🎉", color = SmartCampusColors.OnSurfaceVariant)
                            }
                        }
                    } else {
                        todayClasses.forEach { cls ->
                            ClassCard(
                                timetableClass = cls,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            // ── Upcoming Assignments ─────────────
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SectionHeader(
                        title = "Upcoming Deadlines",
                        actionLabel = "View All",
                        onAction = { onNavigateTo(Screen.Tasks.route) }
                    )
                    if (upcomingAssignments.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SmartCampusColors.SurfaceVariant, RoundedCornerShape(12.dp))
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.CheckCircle, null, tint = SmartCampusColors.PriorityLow, modifier = Modifier.size(32.dp))
                                Spacer(Modifier.height(8.dp))
                                Text("All caught up! No upcoming deadlines.", color = SmartCampusColors.OnSurfaceVariant)
                            }
                        }
                    } else {
                        upcomingAssignments.take(3).forEach { assignment ->
                            AssignmentCard(
                                assignment = assignment,
                                onToggleStatus = {},
                                onEdit = {},
                                onDelete = {},
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────
//  QUICK ACTIONS DATA
// ─────────────────────────────────────────────
data class QuickAction(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

fun quickActions(
    onTasksClick: () -> Unit,
    onNotesClick: () -> Unit,
    onPomodoroClick: () -> Unit,
    onAttendanceClick: () -> Unit,
    onTimetableClick: () -> Unit,
    onAIClick: () -> Unit
): List<QuickAction> = listOf(
    QuickAction("Tasks", Icons.Default.Assignment, SmartCampusColors.PriorityHigh, onTasksClick),
    QuickAction("Notes", Icons.Default.Note, SmartCampusColors.Primary, onNotesClick),
    QuickAction("Focus", Icons.Default.Timer, SmartCampusColors.Secondary, onPomodoroClick),
    QuickAction("Attendance", Icons.Default.FactCheck, SmartCampusColors.Tertiary, onAttendanceClick),
    QuickAction("Timetable", Icons.Default.CalendarToday, SmartCampusColors.PriorityMedium, onTimetableClick),
    QuickAction("AI Chat", Icons.Default.AutoAwesome, SmartCampusColors.PriorityUrgent, onAIClick)
)

@Composable
fun QuickActionCard(action: QuickAction) {
    Column(
        modifier = Modifier
            .width(72.dp)
            .clickable(onClick = action.onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(action.color.copy(alpha = 0.15f))
                .border(1.dp, action.color.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(action.icon, null, tint = action.color, modifier = Modifier.size(26.dp))
        }
        Text(
            text = action.label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
