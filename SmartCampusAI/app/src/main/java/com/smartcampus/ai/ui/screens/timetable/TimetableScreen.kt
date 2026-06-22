package com.smartcampus.ai.ui.screens.timetable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
fun TimetableScreen(
    onNavigateBack: () -> Unit,
    viewModel: TimetableViewModel = hiltViewModel()
) {
    val classesByDay by viewModel.classesByDay.collectAsStateWithLifecycle()
    val selectedDay by viewModel.selectedDay.collectAsStateWithLifecycle()
    val saveState by viewModel.saveState.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<TimetableClass?>(null) }

    Scaffold(
        containerColor = SmartCampusColors.Background,
        topBar = {
            TopAppBar(
                title = { Text("Timetable", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, "Add Class", tint = SmartCampusColors.Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SmartCampusColors.Surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Day selector
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SmartCampusColors.Surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(DayOfWeek.values()) { day ->
                    val dayClasses = classesByDay[day] ?: emptyList()
                    val isSelected = selectedDay == day
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) SmartCampusColors.Primary
                                else SmartCampusColors.SurfaceVariant
                            )
                            .clickable { viewModel.setSelectedDay(day) }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            day.shortName,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                        if (dayClasses.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(androidx.compose.foundation.shape.CircleShape)
                                    .background(
                                        if (isSelected) Color.White.copy(alpha = 0.7f)
                                        else SmartCampusColors.Primary
                                    )
                            )
                        }
                    }
                }
            }

            // Classes for selected day
            val todayClasses = classesByDay[selectedDay] ?: emptyList()

            if (todayClasses.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.EventBusy,
                    title = "No classes on ${selectedDay.displayName}",
                    subtitle = "Add a class to your timetable",
                    actionLabel = "Add Class",
                    onAction = { showAddDialog = true }
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            selectedDay.displayName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    items(todayClasses, key = { it.id }) { cls ->
                        TimetableClassRow(
                            cls = cls,
                            onDelete = { deleteTarget = cls }
                        )
                    }
                }
            }
        }
    }

    // Add Class Dialog
    if (showAddDialog) {
        AddClassDialog(
            selectedDay = selectedDay,
            saveState = saveState,
            onDismiss = { showAddDialog = false; viewModel.clearSaveState() },
            onConfirm = { subject, teacher, room, day, start, end, color ->
                viewModel.addClass(subject, teacher, room, day, start, end, color)
            },
            onSuccess = { showAddDialog = false; viewModel.clearSaveState() }
        )
    }

    // Delete dialog
    deleteTarget?.let { cls ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Remove Class") },
            text = { Text("Remove \"${cls.subject}\" from ${cls.day.displayName}?") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteClass(cls); deleteTarget = null }) {
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
fun TimetableClassRow(cls: TimetableClass, onDelete: () -> Unit) {
    val color = try { Color(android.graphics.Color.parseColor(cls.color)) }
    catch (e: Exception) { SmartCampusColors.Primary }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SmartCampusColors.SurfaceVariant),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(52.dp)
            ) {
                Text(cls.startTime, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = color)
                Box(modifier = Modifier.width(1.dp).height(20.dp).background(color.copy(alpha = 0.4f)))
                Text(cls.endTime, style = MaterialTheme.typography.labelSmall, color = SmartCampusColors.OnSurfaceVariant)
            }
            // Color bar
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(52.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
            // Info
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(cls.subject, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                if (cls.teacher.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Person, null, tint = SmartCampusColors.OnSurfaceVariant, modifier = Modifier.size(12.dp))
                        Text(cls.teacher, style = MaterialTheme.typography.bodySmall, color = SmartCampusColors.OnSurfaceVariant)
                    }
                }
                if (cls.room.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.LocationOn, null, tint = SmartCampusColors.OnSurfaceVariant, modifier = Modifier.size(12.dp))
                        Text(cls.room, style = MaterialTheme.typography.bodySmall, color = SmartCampusColors.OnSurfaceVariant)
                    }
                }
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, "Delete", tint = SmartCampusColors.Error, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClassDialog(
    selectedDay: DayOfWeek,
    saveState: UiState<Unit>?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, DayOfWeek, String, String, String) -> Unit,
    onSuccess: () -> Unit
) {
    var subject by remember { mutableStateOf("") }
    var teacher by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("") }
    var day by remember { mutableStateOf(selectedDay) }
    var startTime by remember { mutableStateOf("08:00") }
    var endTime by remember { mutableStateOf("09:00") }
    var selectedColorIndex by remember { mutableStateOf(0) }

    LaunchedEffect(saveState) {
        if (saveState is UiState.Success) onSuccess()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Class", fontWeight = FontWeight.SemiBold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = subject, onValueChange = { subject = it },
                    label = { Text("Subject *") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SmartCampusColors.Primary)
                )
                OutlinedTextField(
                    value = teacher, onValueChange = { teacher = it },
                    label = { Text("Teacher (optional)") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SmartCampusColors.Primary)
                )
                OutlinedTextField(
                    value = room, onValueChange = { room = it },
                    label = { Text("Room (optional)") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SmartCampusColors.Primary)
                )

                // Day selector
                Text("Day", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(DayOfWeek.values()) { d ->
                        FilterChip(
                            selected = day == d,
                            onClick = { day = d },
                            label = { Text(d.shortName, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SmartCampusColors.Primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Time inputs
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startTime, onValueChange = { startTime = it },
                        label = { Text("Start") }, singleLine = true,
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp),
                        placeholder = { Text("HH:mm") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SmartCampusColors.Primary)
                    )
                    OutlinedTextField(
                        value = endTime, onValueChange = { endTime = it },
                        label = { Text("End") }, singleLine = true,
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp),
                        placeholder = { Text("HH:mm") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SmartCampusColors.Primary)
                    )
                }

                // Color
                Text("Color", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(SmartCampusColors.SubjectColors) { index, color ->
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(color)
                                .border(
                                    if (selectedColorIndex == index) 3.dp else 0.dp,
                                    Color.White,
                                    androidx.compose.foundation.shape.CircleShape
                                )
                                .clickable { selectedColorIndex = index }
                        )
                    }
                }

                AnimatedVisibility(visible = saveState is UiState.Error) {
                    Text((saveState as? UiState.Error)?.message ?: "",
                        color = SmartCampusColors.Error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val colorHex = listOf("#6C63FF","#00D4AA","#FFB74D","#FF5370","#40C4FF","#E040FB","#69F0AE","#FF6E40")
                    onConfirm(subject, teacher, room, day, startTime, endTime, colorHex.getOrElse(selectedColorIndex) { "#6C63FF" })
                },
                colors = ButtonDefaults.buttonColors(containerColor = SmartCampusColors.Primary),
                enabled = saveState !is UiState.Loading
            ) {
                if (saveState is UiState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                } else Text("Add Class")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        containerColor = SmartCampusColors.Surface
    )
}
