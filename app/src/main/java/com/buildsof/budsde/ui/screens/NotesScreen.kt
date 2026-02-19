package com.buildsof.budsde.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.buildsof.budsde.data.Note
import com.buildsof.budsde.data.Priority
import com.buildsof.budsde.data.Task
import com.buildsof.budsde.ui.components.AppCard
import com.buildsof.budsde.ui.components.AppTextField
import androidx.compose.runtime.LaunchedEffect
import com.buildsof.budsde.viewmodel.NotesViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    projectId: String,
    viewModel: NotesViewModel,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(projectId) {
        viewModel.loadProject(projectId)
    }
    
    val project = viewModel.project
    
    if (project == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember { mutableStateOf<String?>(null) }
    var selectedNoteForPhoto by remember { mutableStateOf<String?>(null) }
    
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { photoUri ->
            selectedNoteForPhoto?.let { noteId ->
                viewModel.addPhotoToNote(noteId, photoUri.toString())
            }
        }
        selectedNoteForPhoto = null
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notes & Tasks") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddNoteDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add note")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddNoteDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add note")
            }
        }
    ) { paddingValues ->
        if (project.notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Note,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "No notes yet",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Add notes and tasks to track your progress",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(project.notes) { note ->
                    NoteCard(
                        note = note,
                        onAddTask = { showAddTaskDialog = note.id },
                        onToggleTask = { taskId ->
                            // TODO: Implement toggle task
                        },
                        onAddPhoto = {
                            selectedNoteForPhoto = note.id
                            photoPickerLauncher.launch("image/*")
                        }
                    )
                }
            }
        }
    }
    
    if (showAddNoteDialog) {
        AddNoteDialog(
            onDismiss = { showAddNoteDialog = false },
            onAdd = { noteText ->
                viewModel.addNote(noteText, photoUri = null)
                showAddNoteDialog = false
            }
        )
    }
    
    showAddTaskDialog?.let { noteId ->
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = null },
            onAdd = { title, priority ->
                // TODO: Implement add task functionality
                showAddTaskDialog = null
            }
        )
    }
}

@Composable
fun NoteCard(
    note: Note,
    onAddTask: () -> Unit,
    onToggleTask: (String) -> Unit,
    onAddPhoto: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
    
    AppCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = note.text,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = dateFormat.format(note.createdAt),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.Note,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            if (note.photos.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    note.photos.forEach { photoUri ->
                        Image(
                            painter = rememberAsyncImagePainter(photoUri),
                            contentDescription = "Note photo",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            
            if (note.tasks.isNotEmpty()) {
                Divider()
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    note.tasks.forEach { task ->
                        TaskItem(
                            task = task,
                            onToggle = { onToggleTask(task.id) }
                        )
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onAddTask,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Task")
                }
                
                OutlinedButton(
                    onClick = onAddPhoto,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Photo${if (note.photos.isNotEmpty()) " (${note.photos.size})" else ""}")
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggle() }
                )
                
                Column {
                    Text(
                        text = task.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    task.dueDate?.let { date ->
                        Text(
                            text = "Due: ${SimpleDateFormat("MMM dd", Locale.ENGLISH).format(date)}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            PriorityBadge(task.priority)
        }
    }
}

@Composable
fun PriorityBadge(priority: Priority) {
    val color = when (priority) {
        Priority.HIGH -> MaterialTheme.colorScheme.error
        Priority.MEDIUM -> MaterialTheme.colorScheme.tertiary
        Priority.LOW -> MaterialTheme.colorScheme.primary
    }
    
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.2f)
    ) {
        Text(
            text = priority.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun AddNoteDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var noteText by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Note") },
        text = {
            AppTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = "Note text",
                singleLine = false,
                maxLines = 5
            )
        },
        confirmButton = {
            Button(
                onClick = { if (noteText.isNotBlank()) onAdd(noteText) },
                enabled = noteText.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onAdd: (String, Priority) -> Unit
) {
    var taskTitle by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AppTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = "Task title"
                )
                
                Text("Priority:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Priority.values().forEach { priority ->
                        FilterChip(
                            selected = selectedPriority == priority,
                            onClick = { selectedPriority = priority },
                            label = { Text(priority.name) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (taskTitle.isNotBlank()) onAdd(taskTitle, selectedPriority) },
                enabled = taskTitle.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
