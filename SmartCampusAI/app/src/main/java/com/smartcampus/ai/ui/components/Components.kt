package com.smartcampus.ai.ui.components

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
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.smartcampus.ai.domain.model.*
import com.smartcampus.ai.ui.theme.SmartCampusColors
import java.text.SimpleDateFormat
import java.util.*

// ─────────────────────────────────────────────
//  GRADIENT CARD
// ─────────────────────────────────────────────
@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    gradient: List<Color> = listOf(SmartCampusColors.Primary, SmartCampusColors.PrimaryVariant),
    cornerRadius: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                brush = Brush.linearGradient(
                    colors = gradient,
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            ),
        content = content
    )
}

// ─────────────────────────────────────────────
//  STATS CARD
// ─────────────────────────────────────────────
@Composable
fun StatsCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    subtitle: String = ""
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
//  PRIORITY BADGE
// ─────────────────────────────────────────────
@Composable
fun PriorityBadge(priority: Priority, modifier: Modifier = Modifier) {
    val (color, label) = when (priority) {
        Priority.LOW -> SmartCampusColors.PriorityLow to "Low"
        Priority.MEDIUM -> SmartCampusColors.PriorityMedium to "Medium"
        Priority.HIGH -> SmartCampusColors.PriorityHigh to "High"
        Priority.URGENT -> SmartCampusColors.PriorityUrgent to "Urgent"
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ─────────────────────────────────────────────
//  ASSIGNMENT CARD
// ─────────────────────────────────────────────
@Composable
fun AssignmentCard(
    assignment: Assignment,
    onToggleStatus: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCompleted = assignment.status == AssignmentStatus.COMPLETED
    val daysLeft = ((assignment.deadline - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt()
    val isOverdue = daysLeft < 0 && !isCompleted

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCompleted) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onToggleStatus() },
                colors = CheckboxDefaults.colors(
                    checkedColor = SmartCampusColors.Primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            // Left Color Indicator
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        when (assignment.priority) {
                            Priority.URGENT -> SmartCampusColors.PriorityUrgent
                            Priority.HIGH -> SmartCampusColors.PriorityHigh
                            Priority.MEDIUM -> SmartCampusColors.PriorityMedium
                            Priority.LOW -> SmartCampusColors.PriorityLow
                        }
                    )
            )

            // Content
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = assignment.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isCompleted)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (isCompleted)
                        androidx.compose.ui.text.style.TextDecoration.LineThrough
                    else null
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = assignment.subject,
                        style = MaterialTheme.typography.bodySmall,
                        color = SmartCampusColors.Primary
                    )
                    PriorityBadge(assignment.priority)
                }
                Text(
                    text = when {
                        isCompleted -> "✓ Completed"
                        isOverdue -> "⚠ Overdue by ${-daysLeft} day(s)"
                        daysLeft == 0 -> "🔥 Due today!"
                        daysLeft == 1 -> "⏰ Due tomorrow"
                        else -> "📅 ${daysLeft} days left"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = when {
                        isCompleted -> SmartCampusColors.StatusCompleted
                        isOverdue -> SmartCampusColors.PriorityUrgent
                        daysLeft <= 1 -> SmartCampusColors.PriorityHigh
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            // Actions
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = SmartCampusColors.Error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  NOTE CARD
// ─────────────────────────────────────────────
@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (note.isPinned) {
                        Icon(
                            Icons.Default.PushPin,
                            contentDescription = null,
                            tint = SmartCampusColors.Tertiary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, false)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(SmartCampusColors.Primary.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = note.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = SmartCampusColors.Primary
                    )
                }
                Text(
                    text = SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(note.updatedAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (note.attachmentPath != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AttachFile,
                        contentDescription = null,
                        tint = SmartCampusColors.Secondary,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Attachment",
                        style = MaterialTheme.typography.labelSmall,
                        color = SmartCampusColors.Secondary
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  ATTENDANCE PROGRESS CARD
// ─────────────────────────────────────────────
@Composable
fun AttendanceCard(
    attendance: Attendance,
    onMarkPresent: () -> Unit,
    onMarkAbsent: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val percentage = attendance.percentage
    val color = try { Color(android.graphics.Color.parseColor(attendance.color)) }
    catch (e: Exception) { SmartCampusColors.Primary }
    val animatedProgress by animateFloatAsState(
        targetValue = percentage / 100f,
        animationSpec = tween(durationMillis = 800),
        label = "attendance_progress"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Text(
                        text = attendance.subject,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onMarkPresent, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Present",
                            tint = SmartCampusColors.PriorityLow,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onMarkAbsent, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Absent",
                            tint = SmartCampusColors.Error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Progress bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${attendance.attendedClasses}/${attendance.totalClasses} classes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${percentage.toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (attendance.isShortage) SmartCampusColors.Error else color
                    )
                }
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (attendance.isShortage) SmartCampusColors.Error else color,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                if (attendance.isShortage) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = SmartCampusColors.Error,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Need ${attendance.classesNeededToReach} more class(es) to reach ${attendance.requiredPercentage.toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = SmartCampusColors.Error
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  LOADING INDICATOR
// ─────────────────────────────────────────────
@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = SmartCampusColors.Primary)
    }
}

// ─────────────────────────────────────────────
//  EMPTY STATE
// ─────────────────────────────────────────────
@Composable
fun EmptyStateView(
    icon: ImageVector,
    title: String,
    subtitle: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(SmartCampusColors.Primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SmartCampusColors.Primary,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = SmartCampusColors.Primary)
            ) {
                Text(text = actionLabel)
            }
        }
    }
}

// ─────────────────────────────────────────────
//  SECTION HEADER
// ─────────────────────────────────────────────
@Composable
fun SectionHeader(
    title: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        if (actionLabel != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(
                    text = actionLabel,
                    color = SmartCampusColors.Primary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
//  TIMETABLE CLASS CARD
// ─────────────────────────────────────────────
@Composable
fun ClassCard(
    timetableClass: TimetableClass,
    modifier: Modifier = Modifier
) {
    val color = try { Color(android.graphics.Color.parseColor(timetableClass.color)) }
    catch (e: Exception) { SmartCampusColors.Primary }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = timetableClass.subject,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = color
                )
                Text(
                    text = "${timetableClass.startTime} – ${timetableClass.endTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (timetableClass.room.isNotEmpty()) {
                    Text(
                        text = "📍 ${timetableClass.room}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
