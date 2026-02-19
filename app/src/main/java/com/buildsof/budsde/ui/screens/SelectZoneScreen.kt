package com.buildsof.budsde.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buildsof.budsde.data.Room
import com.buildsof.budsde.data.RoomType
import com.buildsof.budsde.data.Dimensions
import com.buildsof.budsde.ui.components.AppButton
import com.buildsof.budsde.ui.components.IconCard
import com.buildsof.budsde.viewmodel.SelectZoneViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectZoneScreen(
    projectId: String,
    viewModel: SelectZoneViewModel,
    onZoneSelected: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(projectId) {
        viewModel.loadProject(projectId)
    }
    
    var selectedRoomType by remember { mutableStateOf<RoomType?>(null) }
    var calculateWalls by remember { mutableStateOf(true) }
    var calculateFloor by remember { mutableStateOf(true) }
    var calculateCeiling by remember { mutableStateOf(true) }
    
    val roomTypes = listOf(
        RoomType.ROOM to Icons.Default.Bed,
        RoomType.KITCHEN to Icons.Default.Kitchen,
        RoomType.BATHROOM to Icons.Default.Bathtub,
        RoomType.HALLWAY to Icons.Default.MeetingRoom,
        RoomType.BALCONY to Icons.Default.Balcony,
        RoomType.OTHER to Icons.Default.HomeWork
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Room/Zone") },
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
                    text = "Choose Room Type",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.height(240.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(roomTypes) { (roomType, icon) ->
                        IconCard(
                            icon = icon,
                            title = roomType.name.lowercase().capitalize(),
                            onClick = { selectedRoomType = roomType },
                            iconTint = if (selectedRoomType == roomType) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            item {
                Text(
                    text = "What to Calculate",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Walls", fontSize = 16.sp)
                            Switch(
                                checked = calculateWalls,
                                onCheckedChange = { calculateWalls = it }
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Floor", fontSize = 16.sp)
                            Switch(
                                checked = calculateFloor,
                                onCheckedChange = { calculateFloor = it }
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Ceiling", fontSize = 16.sp)
                            Switch(
                                checked = calculateCeiling,
                                onCheckedChange = { calculateCeiling = it }
                            )
                        }
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                AppButton(
                    text = "Continue",
                    onClick = {
                        selectedRoomType?.let { roomType ->
                            val roomName = roomType.name.lowercase().replaceFirstChar { it.uppercase() }
                            viewModel.addRoom(roomType, roomName)
                            // Get the newly added room ID from the updated project
                            viewModel.project?.rooms?.lastOrNull()?.let { newRoom ->
                                onZoneSelected(newRoom.id)
                            }
                        }
                    },
                    enabled = selectedRoomType != null,
                    icon = Icons.Default.ArrowForward
                )
            }
        }
    }
}
