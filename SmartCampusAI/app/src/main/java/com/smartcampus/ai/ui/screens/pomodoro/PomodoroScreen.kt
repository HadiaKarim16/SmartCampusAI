package com.smartcampus.ai.ui.screens.pomodoro

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcampus.ai.ui.components.StatsCard
import com.smartcampus.ai.ui.theme.SmartCampusColors
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(
    onNavigateBack: () -> Unit,
    viewModel: PomodoroViewModel = hiltViewModel()
) {
    val timerState by viewModel.timerState.collectAsStateWithLifecycle()
    val timeLeft by viewModel.timeLeft.collectAsStateWithLifecycle()
    val pomodoroDuration by viewModel.pomodoroDuration.collectAsStateWithLifecycle()
    val breakDuration by viewModel.breakDuration.collectAsStateWithLifecycle()
    val completedSessions by viewModel.completedSessions.collectAsStateWithLifecycle()
    val totalFocusMinutes by viewModel.totalFocusMinutes.collectAsStateWithLifecycle()
    val taskTitle by viewModel.taskTitle.collectAsStateWithLifecycle()

    // Local countdown ticker
    LaunchedEffect(timerState) {
        while (timerState == TimerState.RUNNING) {
            delay(1000L)
            val current = viewModel.timeLeft.value
            if (current > 0) {
                viewModel.updateTimeLeft(current - 1)
            } else {
                viewModel.completeSession(breakDuration)
            }
        }
    }

    val totalSeconds = when (timerState) {
        TimerState.BREAK -> breakDuration * 60
        else -> pomodoroDuration * 60
    }.coerceAtLeast(1)

    val progress = timeLeft.toFloat() / totalSeconds.toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500),
        label = "timer_progress"
    )

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60

    val timerColor = when (timerState) {
        TimerState.BREAK -> SmartCampusColors.Secondary
        TimerState.RUNNING -> SmartCampusColors.Primary
        TimerState.PAUSED -> SmartCampusColors.Tertiary
        else -> SmartCampusColors.OnSurfaceVariant
    }

    Scaffold(
        containerColor = SmartCampusColors.Background,
        topBar = {
            TopAppBar(
                title = { Text("Pomodoro Focus Timer", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SmartCampusColors.Surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // Mode label
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(timerColor.copy(alpha = 0.15f))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = when (timerState) {
                        TimerState.IDLE -> "🍅 Ready to Focus"
                        TimerState.RUNNING -> "🔥 Focus Session"
                        TimerState.PAUSED -> "⏸ Paused"
                        TimerState.BREAK -> "☕ Break Time"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = timerColor
                )
            }

            // Circular Timer
            Box(
                modifier = Modifier.size(260.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 16.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2
                    val center = Offset(size.width / 2, size.height / 2)

                    // Background track
                    drawCircle(
                        color = SmartCampusColors.SurfaceVariant,
                        radius = radius,
                        center = center,
                        style = Stroke(width = strokeWidth)
                    )

                    // Gradient progress arc
                    val sweepAngle = 360f * animatedProgress
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                timerColor.copy(alpha = 0.4f),
                                timerColor,
                                timerColor
                            ),
                            center = center
                        ),
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2)
                    )

                    // Dot at progress end
                    if (sweepAngle > 5f) {
                        val angleRad = (-90f + sweepAngle) * PI.toFloat() / 180f
                        val dotCenter = Offset(
                            center.x + radius * cos(angleRad),
                            center.y + radius * sin(angleRad)
                        )
                        drawCircle(color = timerColor, radius = strokeWidth / 2, center = dotCenter)
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%02d:%02d", minutes, seconds),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = timerColor,
                        fontSize = 52.sp
                    )
                    Text(
                        text = when (timerState) {
                            TimerState.BREAK -> "Break • ${breakDuration}m"
                            else -> "Focus • ${pomodoroDuration}m"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = SmartCampusColors.OnSurfaceVariant
                    )
                }
            }

            // Task input (only when idle)
            AnimatedVisibility(visible = timerState == TimerState.IDLE) {
                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = viewModel::setTaskTitle,
                    label = { Text("What are you focusing on?") },
                    leadingIcon = { Icon(Icons.Default.Assignment, null, tint = SmartCampusColors.Primary) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SmartCampusColors.Primary)
                )
            }

            // Control Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stop (only when active)
                AnimatedVisibility(visible = timerState != TimerState.IDLE) {
                    OutlinedIconButton(
                        onClick = { viewModel.stopTimer() },
                        modifier = Modifier.size(56.dp),
                        border = BorderStroke(2.dp, SmartCampusColors.Error)
                    ) {
                        Icon(Icons.Default.Stop, "Stop", tint = SmartCampusColors.Error, modifier = Modifier.size(24.dp))
                    }
                }

                // Start / Pause / Resume
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                when (timerState) {
                                    TimerState.RUNNING -> listOf(SmartCampusColors.Tertiary, SmartCampusColors.PriorityMedium)
                                    TimerState.BREAK -> listOf(SmartCampusColors.Secondary, SmartCampusColors.Primary)
                                    else -> listOf(SmartCampusColors.Primary, SmartCampusColors.Secondary)
                                }
                            )
                        )
                        .clickable {
                            when (timerState) {
                                TimerState.IDLE -> viewModel.startTimer(pomodoroDuration)
                                TimerState.RUNNING -> viewModel.pauseTimer()
                                TimerState.PAUSED -> viewModel.resumeTimer()
                                TimerState.BREAK -> viewModel.startTimer(breakDuration)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (timerState) {
                            TimerState.RUNNING -> Icons.Default.Pause
                            TimerState.PAUSED -> Icons.Default.PlayArrow
                            TimerState.BREAK -> Icons.Default.Coffee
                            TimerState.IDLE -> Icons.Default.PlayArrow
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Skip break (only during break)
                AnimatedVisibility(visible = timerState == TimerState.BREAK) {
                    OutlinedIconButton(
                        onClick = { viewModel.startTimer(pomodoroDuration) },
                        modifier = Modifier.size(56.dp),
                        border = BorderStroke(2.dp, SmartCampusColors.Primary)
                    ) {
                        Icon(Icons.Default.SkipNext, "Skip Break", tint = SmartCampusColors.Primary, modifier = Modifier.size(24.dp))
                    }
                }
            }

            // Session count bubbles
            if (completedSessions > 0) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Today's Sessions",
                        style = MaterialTheme.typography.labelLarge,
                        color = SmartCampusColors.OnSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        repeat(minOf(completedSessions, 8)) { index ->
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (index < completedSessions) SmartCampusColors.Primary
                                        else SmartCampusColors.SurfaceVariant
                                    )
                            )
                        }
                        if (completedSessions > 8) {
                            Text(
                                "+${completedSessions - 8}",
                                style = MaterialTheme.typography.labelSmall,
                                color = SmartCampusColors.Primary
                            )
                        }
                    }
                }
            }

            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatsCard(
                    title = "Sessions Done",
                    value = "$completedSessions",
                    icon = Icons.Default.CheckCircle,
                    color = SmartCampusColors.PriorityLow,
                    modifier = Modifier.weight(1f)
                )
                StatsCard(
                    title = "Total Focus",
                    value = if (totalFocusMinutes >= 60) "${totalFocusMinutes / 60}h ${totalFocusMinutes % 60}m"
                            else "${totalFocusMinutes}m",
                    icon = Icons.Default.Timer,
                    color = SmartCampusColors.Primary,
                    modifier = Modifier.weight(1f)
                )
            }

            // Tips
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SmartCampusColors.SurfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Default.Lightbulb, null, tint = SmartCampusColors.Tertiary, modifier = Modifier.size(20.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Pomodoro Tips", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = SmartCampusColors.Tertiary)
                        Text(
                            "Work for ${pomodoroDuration} minutes, then take a ${breakDuration}-minute break. After 4 sessions, take a longer 15-30 minute break.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SmartCampusColors.OnSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
