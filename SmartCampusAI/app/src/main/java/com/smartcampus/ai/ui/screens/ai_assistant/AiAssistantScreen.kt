package com.smartcampus.ai.ui.screens.ai_assistant

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcampus.ai.domain.model.AiChatMessage
import com.smartcampus.ai.ui.theme.SmartCampusColors
import java.text.SimpleDateFormat
import java.util.*

val QUICK_PROMPTS = listOf(
    "📚 Summarize key concepts" to "Give me a summary of the key concepts I should know for my exam",
    "📝 Generate a quiz" to "Generate 5 practice quiz questions for my upcoming test",
    "💡 Study tips" to "Give me effective study tips to improve my grades",
    "📅 Study plan" to "Help me create a weekly study plan for my exams",
    "🧠 Explain concept" to "Explain a difficult concept in simple terms",
    "⏰ Time management" to "Give me time management strategies for university students"
)

val SUBJECTS = listOf("General", "Mathematics", "Physics", "Chemistry", "Biology",
    "Computer Science", "History", "Literature", "Economics", "Engineering")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(viewModel: AiAssistantViewModel = hiltViewModel()) {
    val chatHistory by viewModel.chatHistory.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val selectedSubject by viewModel.selectedSubject.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }
    var showSubjectDropdown by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    // Auto-scroll to bottom
    LaunchedEffect(chatHistory.size, isLoading) {
        if (chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(chatHistory.size - 1)
        }
    }

    Scaffold(
        containerColor = SmartCampusColors.Background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SmartCampusColors.Surface)
            ) {
                TopAppBar(
                    title = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(SmartCampusColors.Primary, SmartCampusColors.Secondary)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    "AI Study Assistant",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(SmartCampusColors.PriorityLow)
                                    )
                                    Text(
                                        "Online",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SmartCampusColors.PriorityLow
                                    )
                                }
                            }
                        }
                    },
                    actions = {
                        // Subject selector
                        ExposedDropdownMenuBox(
                            expanded = showSubjectDropdown,
                            onExpandedChange = { showSubjectDropdown = it }
                        ) {
                            TextButton(
                                onClick = { showSubjectDropdown = true },
                                modifier = Modifier.menuAnchor()
                            ) {
                                Text(
                                    selectedSubject,
                                    color = SmartCampusColors.Primary,
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Icon(
                                    if (showSubjectDropdown) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    null,
                                    tint = SmartCampusColors.Primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            ExposedDropdownMenu(
                                expanded = showSubjectDropdown,
                                onDismissRequest = { showSubjectDropdown = false }
                            ) {
                                SUBJECTS.forEach { subject ->
                                    DropdownMenuItem(
                                        text = { Text(subject) },
                                        onClick = {
                                            viewModel.setSubject(subject)
                                            showSubjectDropdown = false
                                        },
                                        leadingIcon = {
                                            if (selectedSubject == subject) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    null,
                                                    tint = SmartCampusColors.Primary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                        if (chatHistory.isNotEmpty()) {
                            IconButton(onClick = { showClearDialog = true }) {
                                Icon(Icons.Default.DeleteSweep, "Clear", tint = SmartCampusColors.OnSurfaceVariant)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = SmartCampusColors.Surface)
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SmartCampusColors.Surface)
                    .padding(12.dp)
            ) {
                // Quick prompts (only when chat is empty)
                if (chatHistory.isEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        items(QUICK_PROMPTS) { (label, prompt) ->
                            AssistChip(
                                onClick = {
                                    inputText = prompt
                                },
                                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = SmartCampusColors.Primary.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }

                // Input Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                "Ask me anything about your studies...",
                                color = SmartCampusColors.OnSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        modifier = Modifier.weight(1f),
                        maxLines = 4,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (inputText.isNotBlank()) {
                                    viewModel.sendMessage(inputText)
                                    inputText = ""
                                }
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SmartCampusColors.Primary,
                            unfocusedBorderColor = SmartCampusColors.SurfaceVariant
                        )
                    )
                    // Send button
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(
                                if (inputText.isNotBlank() && !isLoading)
                                    Brush.linearGradient(
                                        listOf(SmartCampusColors.Primary, SmartCampusColors.Secondary)
                                    )
                                else Brush.linearGradient(
                                    listOf(
                                        SmartCampusColors.SurfaceVariant,
                                        SmartCampusColors.SurfaceVariant
                                    )
                                )
                            )
                            .clickable(enabled = inputText.isNotBlank() && !isLoading) {
                                viewModel.sendMessage(inputText)
                                inputText = ""
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Send,
                                "Send",
                                tint = if (inputText.isNotBlank()) Color.White else SmartCampusColors.OnSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (chatHistory.isEmpty() && !isLoading) {
            // Welcome screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    SmartCampusColors.Primary.copy(alpha = 0.2f),
                                    SmartCampusColors.Secondary.copy(alpha = 0.2f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(100.dp)) {
                        drawCircle(
                            color = SmartCampusColors.Primary.copy(alpha = 0.05f),
                            radius = 200f,
                            center = Offset(size.width / 2, size.height / 2)
                        )
                    }
                    Icon(
                        Icons.Default.AutoAwesome,
                        null,
                        tint = SmartCampusColors.Primary,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(Modifier.height(20.dp))
                Text(
                    "Hi! I'm your AI Study Assistant",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Ask me anything about your coursework. I can help with explanations, summaries, quizzes, study plans, and more!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SmartCampusColors.OnSurfaceVariant
                )
                Spacer(Modifier.height(24.dp))
                // Feature chips
                listOf("📝 Summaries", "🧪 Quiz Generator", "💡 Study Tips", "📅 Study Plans").forEach { feature ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(SmartCampusColors.SurfaceVariant, RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(feature, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(chatHistory.reversed(), key = { it.id }) { chat ->
                    ChatBubbles(chat = chat)
                }
                if (isLoading) {
                    item {
                        TypingIndicator()
                    }
                }
            }
        }
    }

    // Clear dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear Chat History") },
            text = { Text("Are you sure you want to clear all chat history?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearHistory()
                        showClearDialog = false
                    }
                ) { Text("Clear", color = SmartCampusColors.Error) }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("Cancel") }
            },
            containerColor = SmartCampusColors.Surface
        )
    }

    // Error snackbar
    errorMessage?.let { error ->
        LaunchedEffect(error) {
            viewModel.clearError()
        }
    }
}

// ─────────────────────────────────────────────
//  CHAT BUBBLES
// ─────────────────────────────────────────────
@Composable
fun ChatBubbles(chat: AiChatMessage) {
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // User message
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .widthIn(max = 280.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 16.dp, topEnd = 4.dp,
                                bottomStart = 16.dp, bottomEnd = 16.dp
                            )
                        )
                        .background(
                            Brush.linearGradient(
                                listOf(SmartCampusColors.Primary, SmartCampusColors.PrimaryVariant)
                            )
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        chat.userMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
                Text(
                    timeFormat.format(Date(chat.createdAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = SmartCampusColors.OnSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp, end = 4.dp)
                )
            }
        }

        // AI response
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(SmartCampusColors.Primary, SmartCampusColors.Secondary)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AutoAwesome,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.Start) {
                Box(
                    modifier = Modifier
                        .widthIn(max = 280.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 4.dp, topEnd = 16.dp,
                                bottomStart = 16.dp, bottomEnd = 16.dp
                            )
                        )
                        .background(SmartCampusColors.SurfaceVariant)
                        .padding(12.dp)
                ) {
                    Text(
                        chat.aiResponse,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SmartCampusColors.OnSurface
                    )
                }
                Text(
                    "AI Assistant • ${timeFormat.format(Date(chat.createdAt))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = SmartCampusColors.OnSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp, start = 4.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
//  TYPING INDICATOR
// ─────────────────────────────────────────────
@Composable
fun TypingIndicator() {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(SmartCampusColors.Primary, SmartCampusColors.Secondary)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                .background(SmartCampusColors.SurfaceVariant)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("AI is thinking", style = MaterialTheme.typography.bodySmall, color = SmartCampusColors.OnSurfaceVariant)
                CircularProgressIndicator(
                    modifier = Modifier.size(12.dp),
                    strokeWidth = 2.dp,
                    color = SmartCampusColors.Primary
                )
            }
        }
    }
}
