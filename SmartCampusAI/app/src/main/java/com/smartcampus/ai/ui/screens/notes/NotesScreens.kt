package com.smartcampus.ai.ui.screens.notes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartcampus.ai.domain.model.*
import com.smartcampus.ai.ui.components.*
import com.smartcampus.ai.ui.theme.SmartCampusColors

private val NOTE_COLORS = listOf(
    "#1A1A2E", "#1E3A5F", "#1A3A2A", "#3A1A2E", "#2A2A1A", "#1A2A3A"
)
private val PRESET_CATEGORIES = listOf("General", "Lecture", "Assignment", "Research", "Personal", "Exam Prep")

// ─────────────────────────────────────────────
//  NOTES LIST SCREEN
// ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    onAddNote: () -> Unit,
    onEditNote: (Int) -> Unit,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    var deleteTarget by remember { mutableStateOf<Note?>(null) }

    Scaffold(
        containerColor = SmartCampusColors.Background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SmartCampusColors.Surface)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "My Notes",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    FloatingActionButton(
                        onClick = onAddNote,
                        modifier = Modifier.size(44.dp),
                        containerColor = SmartCampusColors.Primary,
                        contentColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp)
                    ) {
                        Icon(Icons.Default.Add, "Add Note", modifier = Modifier.size(20.dp))
                    }
                }
                // Search
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = viewModel::setSearchQuery,
                    placeholder = { Text("Search notes...", color = SmartCampusColors.OnSurfaceVariant) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = SmartCampusColors.OnSurfaceVariant) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Close, null, tint = SmartCampusColors.OnSurfaceVariant)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SmartCampusColors.Primary,
                        unfocusedBorderColor = SmartCampusColors.SurfaceVariant
                    )
                )
                // Category chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { viewModel.setCategory(cat) },
                            label = { Text(cat, style = MaterialTheme.typography.labelMedium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SmartCampusColors.Primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        if (notes.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.Note,
                title = "No notes yet",
                subtitle = "Create your first note to get started",
                actionLabel = "Create Note",
                onAction = onAddNote,
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notes, key = { it.id }) { note ->
                    NoteCard(
                        note = note,
                        onClick = { onEditNote(note.id) },
                        onDelete = { deleteTarget = note }
                    )
                }
            }
        }
    }

    deleteTarget?.let { note ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Delete Note") },
            text = { Text("Delete \"${note.title}\"?") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteNote(note); deleteTarget = null }) {
                    Text("Delete", color = SmartCampusColors.Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("Cancel") }
            },
            containerColor = SmartCampusColors.Surface
        )
    }
}

// ─────────────────────────────────────────────
//  ADD / EDIT NOTE SCREEN
// ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    noteId: Int,
    onNavigateBack: () -> Unit,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val editingNote by viewModel.editingNote.collectAsStateWithLifecycle()
    val saveState by viewModel.saveState.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("General") }
    var isPinned by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf(NOTE_COLORS.first()) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var customCategory by remember { mutableStateOf("") }

    LaunchedEffect(noteId) { viewModel.loadNote(noteId) }
    LaunchedEffect(editingNote) {
        editingNote?.let {
            title = it.title; content = it.content
            category = it.category; isPinned = it.isPinned
            selectedColor = it.color
        }
    }
    LaunchedEffect(saveState) {
        if (saveState is UiState.Success) { viewModel.clearSaveState(); onNavigateBack() }
    }

    Scaffold(
        containerColor = SmartCampusColors.Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (noteId == -1) "New Note" else "Edit Note",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { isPinned = !isPinned }) {
                        Icon(
                            if (isPinned) Icons.Default.PushPin else Icons.Default.PushPin,
                            "Pin",
                            tint = if (isPinned) SmartCampusColors.Tertiary else SmartCampusColors.OnSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = {
                            viewModel.saveNote(title, content, category, isPinned, noteId)
                        }
                    ) {
                        Icon(Icons.Default.Save, "Save", tint = SmartCampusColors.Primary)
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Note Title *") },
                leadingIcon = { Icon(Icons.Default.Title, null, tint = SmartCampusColors.Primary) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SmartCampusColors.Primary)
            )

            // Category row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Category:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                LazyRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(PRESET_CATEGORIES) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SmartCampusColors.Primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    item {
                        AssistChip(
                            onClick = { showCategoryDialog = true },
                            label = { Text("+ Custom", style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }

            // Color picker
            Text("Note Color", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NOTE_COLORS.forEach { colorHex ->
                    val color = try { Color(android.graphics.Color.parseColor(colorHex)) }
                    catch (e: Exception) { SmartCampusColors.Surface }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(color, shape = androidx.compose.foundation.shape.CircleShape)
                            .border(
                                width = if (selectedColor == colorHex) 3.dp else 0.dp,
                                color = SmartCampusColors.Primary,
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                            .clickable { selectedColor = colorHex }
                    )
                }
            }

            // Content
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Note Content") },
                placeholder = { Text("Start writing your notes here...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp),
                maxLines = 30,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SmartCampusColors.Primary)
            )

            // Error
            AnimatedVisibility(visible = saveState is UiState.Error) {
                val msg = (saveState as? UiState.Error)?.message ?: ""
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SmartCampusColors.Error.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Error, null, tint = SmartCampusColors.Error, modifier = Modifier.size(16.dp))
                    Text(msg, color = SmartCampusColors.Error, style = MaterialTheme.typography.bodySmall)
                }
            }

            Button(
                onClick = { viewModel.saveNote(title, content, category, isPinned, noteId) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SmartCampusColors.Primary),
                enabled = saveState !is UiState.Loading
            ) {
                if (saveState is UiState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Save, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(if (noteId == -1) "Save Note" else "Update Note", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { Text("Custom Category") },
            text = {
                OutlinedTextField(
                    value = customCategory,
                    onValueChange = { customCategory = it },
                    label = { Text("Category Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (customCategory.isNotBlank()) { category = customCategory.trim() }
                    showCategoryDialog = false
                    customCategory = ""
                }) { Text("Add", color = SmartCampusColors.Primary) }
            },
            dismissButton = {
                TextButton(onClick = { showCategoryDialog = false }) { Text("Cancel") }
            },
            containerColor = SmartCampusColors.Surface
        )
    }
}
