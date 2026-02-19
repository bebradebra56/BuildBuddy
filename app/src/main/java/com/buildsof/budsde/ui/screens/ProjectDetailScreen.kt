package com.buildsof.budsde.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.buildsof.budsde.ui.components.AppCard
import com.buildsof.budsde.ui.components.InfoRow
import com.buildsof.budsde.ui.components.SectionHeader
import com.buildsof.budsde.viewmodel.ProjectDetailViewModel
import com.buildsof.budsde.utils.MaterialCalculator
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectId: String,
    viewModel: ProjectDetailViewModel,
    onNavigateToNotes: () -> Unit,
    onNavigateToShoppingList: () -> Unit,
    onNavigateToRoom: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(projectId) {
        viewModel.loadProject(projectId)
    }
    
    val project = viewModel.project
    val settings by viewModel.settings.collectAsState()
    val currency = settings.currency
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
    
    if (project == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    
    val totalBudget = project.budget
    
    var selectedTab by remember { mutableStateOf(0) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(project.name) }
    var editAddress by remember { mutableStateOf(project.address) }
    
    LaunchedEffect(project) {
        editName = project.name
        editAddress = project.address
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(project.name) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        editName = project.name
                        editAddress = project.address
                        showEditDialog = true 
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = {
                        viewModel.deleteProject(onDeleted = {
                            onNavigateBack()
                        })
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Overview") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Rooms") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Shopping") }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Notes") }
                )
            }
            
            when (selectedTab) {
                0 -> OverviewTab(project, totalBudget, currency, dateFormat)
                1 -> RoomsTab(project, onNavigateToRoom)
                2 -> ShoppingTab(project, viewModel, currency, onNavigateToShoppingList)
                3 -> NotesTab(project, onNavigateToNotes)
            }
        }
    }
    
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Project") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    com.buildsof.budsde.ui.components.AppTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = "Project Name"
                    )
                    com.buildsof.budsde.ui.components.AppTextField(
                        value = editAddress,
                        onValueChange = { editAddress = it },
                        label = "Address"
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isNotBlank()) {
                            viewModel.updateProject(
                                name = editName,
                                address = editAddress
                            )
                            showEditDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun OverviewTab(
    project: com.buildsof.budsde.data.Project,
    totalBudget: Double,
    currency: com.buildsof.budsde.data.Currency,
    dateFormat: SimpleDateFormat
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        project.photoUri?.let { photoUri ->
            item {
                Image(
                    painter = rememberAsyncImagePainter(photoUri),
                    contentDescription = "Project photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Project Summary",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    InfoRow("Start Date:", dateFormat.format(project.startDate))
                    InfoRow("Currency:", project.currency.name)
                    if (project.address.isNotEmpty()) {
                        InfoRow("Location:", project.address)
                    }
                }
            }
        }
        
        item {
            SectionHeader("Budget & Costs")
        }
        
        item {
            AppCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (project.budget > 0) {
                        InfoRow(
                            "Budget:",
                            "${currency.symbol}${String.format("%.2f", project.budget)}"
                        )
                    }
                    InfoRow(
                        "Estimated Cost:",
                        "${currency.symbol}${String.format("%.2f", totalBudget)}"
                    )
                    
                    if (project.budget > 0) {
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                        val remaining = project.budget - totalBudget
                        val isOverBudget = remaining < 0
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isOverBudget) "Over Budget:" else "Remaining:",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOverBudget) 
                                    MaterialTheme.colorScheme.error 
                                else 
                                    MaterialTheme.colorScheme.tertiary
                            )
                            Text(
                                text = "${currency.symbol}${String.format("%.2f", kotlin.math.abs(remaining))}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOverBudget) 
                                    MaterialTheme.colorScheme.error 
                                else 
                                    MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }
        }
        
        item {
            SectionHeader("Progress")
        }
        
        item {
            AppCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Rooms:")
                        Text(
                            text = "${project.rooms.size}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    val totalWorks = project.rooms.sumOf { it.workItems.size }
                    val completedWorks = project.rooms.sumOf { room ->
                        room.workItems.count { it.config != null }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Configured Works:")
                        Text(
                            text = "$completedWorks / $totalWorks",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    if (totalWorks > 0) {
                        LinearProgressIndicator(
                            progress = { completedWorks.toFloat() / totalWorks },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RoomsTab(
    project: com.buildsof.budsde.data.Project,
    onNavigateToRoom: (String) -> Unit
) {
    if (project.rooms.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MeetingRoom,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
                Text("No rooms yet")
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(project.rooms) { room ->
                AppCard(onClick = { onNavigateToRoom(room.id) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = room.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${room.dimensions.floorArea.let { String.format("%.2f", it) }} m²",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${room.workItems.filter { it.enabled }.size} works",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "View room"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShoppingTab(
    project: com.buildsof.budsde.data.Project,
    viewModel: com.buildsof.budsde.viewmodel.ProjectDetailViewModel,
    currency: com.buildsof.budsde.data.Currency,
    onNavigateToShoppingList: () -> Unit
) {
    val shoppingList = remember(project) {
        MaterialCalculator.calculateShoppingList(project)
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Button(
                onClick = onNavigateToShoppingList,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open Full Shopping List")
            }
        }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Quick Summary",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    InfoRow("Total Items:", "${shoppingList.size}")
                    InfoRow(
                        "Total Cost:",
                        "${currency.symbol}${String.format("%.2f", shoppingList.sumOf { it.price })}"
                    )
                }
            }
        }
        
        items(shoppingList.take(5)) { item ->
            AppCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${String.format("%.2f", item.quantity)} ${item.unit}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "${currency.symbol}${String.format("%.2f", item.price)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        if (shoppingList.size > 5) {
            item {
                Text(
                    text = "... and ${shoppingList.size - 5} more items",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun NotesTab(
    project: com.buildsof.budsde.data.Project,
    onNavigateToNotes: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onNavigateToNotes,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Note, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Open Notes & Tasks")
        }
        
        AppCard {
            Column {
                Text(
                    text = "Notes:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${project.notes.size} notes",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                val totalTasks = project.notes.sumOf { it.tasks.size }
                val completedTasks = project.notes.sumOf { note ->
                    note.tasks.count { it.isCompleted }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tasks:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$completedTasks / $totalTasks completed",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
