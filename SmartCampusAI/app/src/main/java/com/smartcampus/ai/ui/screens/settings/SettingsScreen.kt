package com.smartcampus.ai.ui.screens.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcampus.ai.ui.theme.SmartCampusColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle()
    val pomodoroDuration by viewModel.pomodoroDuration.collectAsStateWithLifecycle()
    val breakDuration by viewModel.breakDuration.collectAsStateWithLifecycle()
    val attendanceThreshold by viewModel.attendanceThreshold.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()

    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = SmartCampusColors.Background,
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SmartCampusColors.Surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SmartCampusColors.Surface),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userName.ifEmpty { "Student" },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = userEmail.ifEmpty { "student@university.edu" },
                            style = MaterialTheme.typography.bodySmall,
                            color = SmartCampusColors.OnSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(SmartCampusColors.Primary.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "Student",
                                style = MaterialTheme.typography.labelSmall,
                                color = SmartCampusColors.Primary
                            )
                        }
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Edit, "Edit", tint = SmartCampusColors.OnSurfaceVariant)
                    }
                }
            }

            // Appearance
            SettingsSection(title = "Appearance") {
                SettingsToggleItem(
                    icon = Icons.Default.DarkMode,
                    iconColor = SmartCampusColors.Primary,
                    title = "Dark Mode",
                    subtitle = "Switch between dark and light theme",
                    checked = isDarkMode,
                    onCheckedChange = viewModel::setDarkMode
                )
            }

            // Notifications
            SettingsSection(title = "Notifications") {
                SettingsToggleItem(
                    icon = Icons.Default.Notifications,
                    iconColor = SmartCampusColors.Tertiary,
                    title = "Enable Notifications",
                    subtitle = "Task reminders and class alerts",
                    checked = notificationsEnabled,
                    onCheckedChange = viewModel::setNotifications
                )
            }

            // Pomodoro Settings
            SettingsSection(title = "Pomodoro Timer") {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Focus duration
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Timer, null, tint = SmartCampusColors.Primary, modifier = Modifier.size(18.dp))
                                Text("Focus Duration", style = MaterialTheme.typography.bodyLarge)
                            }
                            Text(
                                "$pomodoroDuration min",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = SmartCampusColors.Primary
                            )
                        }
                        Slider(
                            value = pomodoroDuration.toFloat(),
                            onValueChange = { viewModel.setPomodoroDuration(it.toInt()) },
                            valueRange = 10f..60f,
                            steps = 9,
                            colors = SliderDefaults.colors(thumbColor = SmartCampusColors.Primary, activeTrackColor = SmartCampusColors.Primary)
                        )
                    }

                    Divider(color = SmartCampusColors.SurfaceVariant)

                    // Break duration
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Coffee, null, tint = SmartCampusColors.Secondary, modifier = Modifier.size(18.dp))
                                Text("Break Duration", style = MaterialTheme.typography.bodyLarge)
                            }
                            Text(
                                "$breakDuration min",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = SmartCampusColors.Secondary
                            )
                        }
                        Slider(
                            value = breakDuration.toFloat(),
                            onValueChange = { viewModel.setBreakDuration(it.toInt()) },
                            valueRange = 1f..20f,
                            steps = 18,
                            colors = SliderDefaults.colors(thumbColor = SmartCampusColors.Secondary, activeTrackColor = SmartCampusColors.Secondary)
                        )
                    }
                }
            }

            // Attendance Settings
            SettingsSection(title = "Attendance") {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FactCheck, null, tint = SmartCampusColors.PriorityMedium, modifier = Modifier.size(18.dp))
                            Text("Required Attendance", style = MaterialTheme.typography.bodyLarge)
                        }
                        Text(
                            "${attendanceThreshold.toInt()}%",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = SmartCampusColors.PriorityMedium
                        )
                    }
                    Slider(
                        value = attendanceThreshold,
                        onValueChange = viewModel::setAttendanceThreshold,
                        valueRange = 50f..100f,
                        steps = 9,
                        colors = SliderDefaults.colors(thumbColor = SmartCampusColors.PriorityMedium, activeTrackColor = SmartCampusColors.PriorityMedium)
                    )
                }
            }

            // About
            SettingsSection(title = "About") {
                SettingsNavItem(icon = Icons.Default.Info, iconColor = SmartCampusColors.Primary, title = "App Version", subtitle = "SmartCampus AI v1.0.0", onClick = {})
                SettingsNavItem(icon = Icons.Default.Policy, iconColor = SmartCampusColors.OnSurfaceVariant, title = "Privacy Policy", onClick = {})
                SettingsNavItem(icon = Icons.Default.Star, iconColor = SmartCampusColors.Tertiary, title = "Rate the App", onClick = {})
            }

            // Logout
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SmartCampusColors.Error.copy(alpha = 0.1f),
                    contentColor = SmartCampusColors.Error
                )
            ) {
                Icon(Icons.Default.Logout, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Sign Out", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sign Out") },
            text = { Text("Are you sure you want to sign out of SmartCampus AI?") },
            confirmButton = {
                TextButton(onClick = { viewModel.logout(onLogout) }) {
                    Text("Sign Out", color = SmartCampusColors.Error, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            },
            containerColor = SmartCampusColors.Surface
        )
    }
}

// ─────────────────────────────────────────────
//  SETTINGS COMPONENTS
// ─────────────────────────────────────────────
@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.labelLarge,
            color = SmartCampusColors.Primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SmartCampusColors.Surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun SettingsToggleItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(18.dp))
            }
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = SmartCampusColors.OnSurfaceVariant)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = SmartCampusColors.Primary, checkedTrackColor = SmartCampusColors.Primary.copy(alpha = 0.3f))
        )
    }
}

@Composable
fun SettingsNavItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String = "",
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(18.dp))
            }
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                if (subtitle.isNotEmpty()) {
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = SmartCampusColors.OnSurfaceVariant)
                }
            }
        }
        Icon(Icons.Default.ChevronRight, null, tint = SmartCampusColors.OnSurfaceVariant, modifier = Modifier.size(18.dp))
    }
}
