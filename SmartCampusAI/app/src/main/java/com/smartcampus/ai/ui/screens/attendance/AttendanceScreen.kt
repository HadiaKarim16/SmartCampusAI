package com.smartcampus.ai.ui.screens.attendance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcampus.ai.domain.model.*
import com.smartcampus.ai.ui.components.*
import com.smartcampus.ai.ui.theme.SmartCampusColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    onNavigateBack: () -> Unit,
    viewModel: AttendanceViewModel = hiltViewModel()
) {
    val attendanceList by viewModel.attendanceList.collectAsStateWithLifecycle()
    val saveState by viewModel.saveState.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<Attendance?>(null) }

    // Overall stats
    val overall = if (attendanceList.isEmpty()) 0f
    else attendanceList.map { it.percentage }.average().toFloat()
    val shortageCount = attendanceList.count { it.isShortage }

    Scaffold(
        containerColor = SmartCampusColors.Background,
        topBar = {
            TopAppBar(
                title = { Text("Attendance Tracker", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, "Add Subject", tint = SmartCampusColors.Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SmartCampusColors.Surface)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Overview card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SmartCampusColors.Surface),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Semester Overview", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatsCard(
                                title = "Overall",
                                value = "${overall.toInt()}%",
                                icon = Icons.Default.FactCheck,
                                color = if (overall >= 75f) SmartCampusColors.PriorityLow else SmartCampusColors.Error,
                                modifier = Modifier.weight(1f)
                            )
                            StatsCard(
                                title = "Subjects",
                                value = "${attendanceList.size}",
                                icon = Icons.Default.Book,
                                color = SmartCampusColors.Primary,
                                modifier = Modifier.weight(1f)
                            )
                            StatsCard(
                                title = "Shortage",
                                value = "$shortageCount",
                                icon = Icons.Default.Warning,
                                color = if (shortageCount > 0) SmartCampusColors.Error else SmartCampusColors.PriorityLow,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (shortageCount > 0) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SmartCampusColors.Error.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, null, tint = SmartCampusColors.Error, modifier = Modifier.size(16.dp))
                                Text(
                                    "$shortageCount subject(s) below required attendance",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SmartCampusColors.Error
                                )
                            }
                        }
                    }
                }
            }

            // Subject list
            if (attendanceList.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.FactCheck,
                        title = "No subjects added",
                        subtitle = "Add subjects to start tracking attendance",
                        actionLabel = "Add Subject",
                        onAction = { showAddDialog = true }
                    )
                }
            } else {
                items(attendanceList, key = { it.id }) { attendance ->
                    AttendanceCard(
                        attendance = attendance,
                        onMarkPresent = { viewModel.markPresent(attendance.id) },
                        onMarkAbsent = { viewModel.markAbsent(attendance.id) },
                        onDelete = { deleteTarget = attendance }
                    )
                }
            }
        }
    }

    // Add subject dialog
    if (showAddDialog) {
        AddSubjectDialog(
            onDismiss = { showAddDialog = false; viewModel.clearSaveState() },
            onConfirm = { name, pct, color ->
                viewModel.addSubject(name, pct, color)
            },
            saveState = saveState,
            onSuccess = { showAddDialog = false; viewModel.clearSaveState() }
        )
    }

    deleteTarget?.let { att ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Remove Subject") },
            text = { Text("Remove \"${att.subject}\" and all its attendance records?") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteSubject(att); deleteTarget = null }) {
                    Text("Remove", color = SmartCampusColors.Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("Cancel") }
            },
            containerColor = SmartCampusColors.Surface
        )
    }
}

@Composable
fun AddSubjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Float, String) -> Unit,
    saveState: UiState<Unit>?,
    onSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var requiredPct by remember { mutableStateOf(75f) }
    var selectedColor by remember { mutableStateOf(SmartCampusColors.SubjectColors.first()) }

    LaunchedEffect(saveState) {
        if (saveState is UiState.Success) onSuccess()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Subject", fontWeight = FontWeight.SemiBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Subject Name *") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SmartCampusColors.Primary)
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "Required Attendance: ${requiredPct.toInt()}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Slider(
                        value = requiredPct,
                        onValueChange = { requiredPct = it },
                        valueRange = 50f..100f,
                        steps = 9,
                        colors = SliderDefaults.colors(thumbColor = SmartCampusColors.Primary, activeTrackColor = SmartCampusColors.Primary)
                    )
                }
                Text("Color", style = MaterialTheme.typography.bodyMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(SmartCampusColors.SubjectColors) { color ->
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(color)
                                .border(
                                    width = if (selectedColor == color) 3.dp else 0.dp,
                                    color = Color.White,
                                    shape = androidx.compose.foundation.shape.CircleShape
                                )
                                .clickable { selectedColor = color }
                        )
                    }
                }

                AnimatedVisibility(visible = saveState is UiState.Error) {
                    val msg = (saveState as? UiState.Error)?.message ?: ""
                    Text(msg, color = SmartCampusColors.Error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val hex = String.format("#%06X", (0xFFFFFF and selectedColor.hashCode()))
                    onConfirm(name, requiredPct, "#6C63FF")
                },
                colors = ButtonDefaults.buttonColors(containerColor = SmartCampusColors.Primary),
                enabled = saveState !is UiState.Loading
            ) {
                if (saveState is UiState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                } else {
                    Text("Add Subject")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        containerColor = SmartCampusColors.Surface
    )
}
