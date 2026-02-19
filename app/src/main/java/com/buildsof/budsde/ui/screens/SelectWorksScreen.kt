package com.buildsof.budsde.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buildsof.budsde.data.WorkType
import com.buildsof.budsde.ui.components.AppButton
import com.buildsof.budsde.viewmodel.SelectWorksViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectWorksScreen(
    projectId: String,
    roomId: String,
    viewModel: SelectWorksViewModel,
    onWorkConfigureClick: (String, WorkType) -> Unit,
    onContinue: () -> Unit,
    onNavigateBack: () -> Unit,
    onEditDimensions: () -> Unit = {}
) {
    // Load project and room data
    LaunchedEffect(projectId, roomId) {
        viewModel.loadProjectAndRoom(projectId, roomId)
    }
    
    val room = viewModel.room
    
    val workItems = remember {
        mutableStateMapOf<WorkType, Boolean>()
    }
    
    // Update workItems when room changes
    LaunchedEffect(room) {
        WorkType.values().forEach { workType ->
            workItems[workType] = room?.workItems?.any { it.type == workType } ?: false
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Works") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEditDimensions) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Dimensions")
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "What Work Needs to Be Done?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Select the tasks and configure each one",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            items(WorkType.values().toList()) { workType ->
                val workItem = room?.workItems?.find { it.type == workType }
                
                WorkItemCard(
                    workType = workType,
                    isEnabled = workItems[workType] ?: false,
                    onToggle = { enabled ->
                        workItems[workType] = enabled
                        viewModel.toggleWorkItem(workType, enabled)
                    },
                    onConfigure = {
                        val item = room?.workItems?.find { it.type == workType }
                        if (item != null) {
                            onWorkConfigureClick(item.id, workType)
                        }
                    },
                    hasConfig = workItem?.config != null
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                AppButton(
                    text = "Continue to Preview",
                    onClick = onContinue,
                    enabled = workItems.values.any { it },
                    icon = Icons.Default.ArrowForward
                )
            }
        }
    }
}

@Composable
fun WorkItemCard(
    workType: WorkType,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    onConfigure: () -> Unit,
    hasConfig: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = workType.name.replace("_", " ").lowercase()
                            .split(" ")
                            .joinToString(" ") { it.capitalize() },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = getWorkDescription(workType),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Switch(
                    checked = isEnabled,
                    onCheckedChange = onToggle
                )
            }
            
            AnimatedVisibility(visible = isEnabled) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onConfigure,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = if (hasConfig) Icons.Default.Edit else Icons.Default.Settings,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (hasConfig) "Edit Configuration" else "Configure")
                    }
                }
            }
        }
    }
}

fun getWorkDescription(workType: WorkType): String {
    return when (workType) {
        WorkType.PAINT_WALLS -> "Paint or repaint wall surfaces"
        WorkType.WALLPAPER -> "Apply wallpaper to walls"
        WorkType.WALL_TILES -> "Install tiles on wall surfaces"
        WorkType.FLOOR_TILES -> "Install tiles on floor"
        WorkType.LAMINATE -> "Install laminate or vinyl flooring"
        WorkType.BASEBOARD -> "Install or replace baseboards"
        WorkType.CEILING_PAINT -> "Paint ceiling surface"
        WorkType.PRIMER -> "Apply primer before painting"
        WorkType.PUTTY -> "Apply putty to smooth surfaces"
    }
}
