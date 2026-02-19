package com.buildsof.budsde.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import com.buildsof.budsde.ui.components.AppButton
import com.buildsof.budsde.ui.components.AppCard
import com.buildsof.budsde.ui.components.InfoRow
import com.buildsof.budsde.ui.components.SectionHeader
import com.buildsof.budsde.viewmodel.PreviewViewModel
import com.buildsof.budsde.utils.MaterialCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    projectId: String,
    roomId: String,
    viewModel: PreviewViewModel,
    onContinue: () -> Unit,
    onEdit: () -> Unit,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(projectId, roomId) {
        viewModel.loadProjectAndRoom(projectId, roomId)
    }
    
    val project = viewModel.project
    val room = viewModel.room
    val settings by viewModel.settings.collectAsState()
    val currency = settings.currency
    val units = settings.units
    
    if (room == null || project == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    // Calculate materials for this room only (preview)
    val materials = remember(project, room) {
        val tempProject = project.copy(rooms = listOf(room))
        MaterialCalculator.calculateShoppingList(tempProject)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preview & Summary") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = room.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                SectionHeader("Dimensions")
            }
            
            item {
                AppCard {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        InfoRow(
                            "Length:",
                            com.buildsof.budsde.utils.FormatUtils.formatDistance(room.dimensions.length, units)
                        )
                        InfoRow(
                            "Width:",
                            com.buildsof.budsde.utils.FormatUtils.formatDistance(room.dimensions.width, units)
                        )
                        InfoRow(
                            "Height:",
                            com.buildsof.budsde.utils.FormatUtils.formatDistance(room.dimensions.height, units)
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        InfoRow(
                            "Floor Area:",
                            com.buildsof.budsde.utils.FormatUtils.formatArea(room.dimensions.floorArea, units)
                        )
                        InfoRow(
                            "Wall Area:",
                            com.buildsof.budsde.utils.FormatUtils.formatArea(room.dimensions.wallArea, units)
                        )
                        InfoRow(
                            "Ceiling Area:",
                            com.buildsof.budsde.utils.FormatUtils.formatArea(room.dimensions.ceilingArea, units)
                        )
                        
                        if (room.marginPercent > 0) {
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            InfoRow(
                                "Buffer/Margin:",
                                "${room.marginPercent}%"
                            )
                        }
                    }
                }
            }
            
            item {
                SectionHeader("Materials Needed")
            }
            
            if (materials.isEmpty()) {
                item {
                    AppCard {
                        Text(
                            text = "No materials calculated yet",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(materials) { material ->
                    AppCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = material.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${String.format("%.2f", material.quantity)} ${material.unit}",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            Text(
                                text = currency.symbol + String.format("%.2f", material.price),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edit")
                    }
                    
                    Button(
                        onClick = onContinue,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Continue")
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null)
                    }
                }
            }
        }
    }
}
