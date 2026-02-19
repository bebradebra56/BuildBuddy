package com.buildsof.budsde.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buildsof.budsde.data.Dimensions
import com.buildsof.budsde.ui.components.*
import com.buildsof.budsde.viewmodel.EnterDimensionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterDimensionsScreen(
    projectId: String,
    roomId: String,
    viewModel: EnterDimensionsViewModel,
    onContinue: () -> Unit,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(projectId, roomId) {
        viewModel.loadProjectAndRoom(projectId, roomId)
    }
    
    val room = viewModel.room
    val settings by viewModel.settings.collectAsState()
    
    var length by remember { mutableStateOf(room?.dimensions?.length?.toString() ?: "") }
    var width by remember { mutableStateOf(room?.dimensions?.width?.toString() ?: "") }
    var height by remember { mutableStateOf(room?.dimensions?.height?.toString() ?: "") }
    var doors by remember { mutableStateOf(room?.doors ?: 1) }
    var windows by remember { mutableStateOf(room?.windows ?: 1) }
    var useStandardSizes by remember { mutableStateOf(true) }
    var doorArea by remember { mutableStateOf(room?.doorArea?.toString() ?: "2.0") }
    
    var windowArea by remember { mutableStateOf(room?.windowArea?.toString() ?: "1.5") }
    var margin by remember { mutableStateOf(room?.marginPercent ?: 10) }
    
    LaunchedEffect(room) {
        room?.let {
            length = it.dimensions.length.toString()
            width = it.dimensions.width.toString()
            height = it.dimensions.height.toString()
            doors = it.doors
            windows = it.windows
            doorArea = it.doorArea.toString()
            windowArea = it.windowArea.toString()
            margin = it.marginPercent
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Enter Dimensions") },
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
                    text = "Room Dimensions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                AppTextField(
                    value = length,
                    onValueChange = { length = it },
                    label = "Length (${com.buildsof.budsde.utils.FormatUtils.getDistanceLabel(settings.units)}) *"
                )
            }
            
            item {
                AppTextField(
                    value = width,
                    onValueChange = { width = it },
                    label = "Width (${com.buildsof.budsde.utils.FormatUtils.getDistanceLabel(settings.units)}) *"
                )
            }
            
            item {
                AppTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = "Height (${com.buildsof.budsde.utils.FormatUtils.getDistanceLabel(settings.units)}) *"
                )
            }
            
            item {
                Text(
                    text = "Openings",
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
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CounterInput(
                            label = "Number of Doors",
                            value = doors,
                            onValueChange = { doors = it }
                        )
                        
                        CounterInput(
                            label = "Number of Windows",
                            value = windows,
                            onValueChange = { windows = it }
                        )
                    }
                }
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Use Standard Sizes",
                                fontSize = 16.sp
                            )
                            Switch(
                                checked = useStandardSizes,
                                onCheckedChange = { useStandardSizes = it }
                            )
                        }
                        
                        if (!useStandardSizes) {
                            Spacer(modifier = Modifier.height(8.dp))
                            AppTextField(
                                value = doorArea,
                                onValueChange = { doorArea = it },
                                label = "Door Area (m²)"
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            AppTextField(
                                value = windowArea,
                                onValueChange = { windowArea = it },
                                label = "Window Area (m²)"
                            )
                        } else {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Standard door: 2.0 m² | Standard window: 1.5 m²",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            item {
                Text(
                    text = "Margin/Buffer",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            item {
                PercentageSelector(
                    selectedPercent = margin,
                    onPercentSelected = { margin = it }
                )
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
                        val l = length.toDoubleOrNull() ?: 0.0
                        val w = width.toDoubleOrNull() ?: 0.0
                        val h = height.toDoubleOrNull() ?: 0.0
                        
                        // Convert to meters if input is in cm
                        val lengthM = if (settings.units == com.buildsof.budsde.data.MeasurementUnit.CENTIMETERS) l / 100.0 else l
                        val widthM = if (settings.units == com.buildsof.budsde.data.MeasurementUnit.CENTIMETERS) w / 100.0 else w
                        val heightM = if (settings.units == com.buildsof.budsde.data.MeasurementUnit.CENTIMETERS) h / 100.0 else h
                        
                        if (lengthM > 0 && widthM > 0) {
                            InfoRow("Floor Area:", com.buildsof.budsde.utils.FormatUtils.formatArea(lengthM * widthM, settings.units))
                        }
                        
                        if (lengthM > 0 && widthM > 0 && heightM > 0) {
                            InfoRow("Wall Area:", com.buildsof.budsde.utils.FormatUtils.formatArea(2 * (lengthM + widthM) * heightM, settings.units))
                            InfoRow("Ceiling Area:", com.buildsof.budsde.utils.FormatUtils.formatArea(lengthM * widthM, settings.units))
                        }
                        
                        if (margin > 0) {
                            InfoRow("Buffer Added:", "$margin%")
                        }
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                AppButton(
                    text = "Continue",
                    onClick = {
                        val l = length.toDoubleOrNull() ?: 0.0
                        val w = width.toDoubleOrNull() ?: 0.0
                        val h = height.toDoubleOrNull() ?: 0.0
                        
                        if (l > 0 && w > 0 && h > 0) {
                            // Convert to meters if input is in cm
                            val lengthM = if (settings.units == com.buildsof.budsde.data.MeasurementUnit.CENTIMETERS) l / 100.0 else l
                            val widthM = if (settings.units == com.buildsof.budsde.data.MeasurementUnit.CENTIMETERS) w / 100.0 else w
                            val heightM = if (settings.units == com.buildsof.budsde.data.MeasurementUnit.CENTIMETERS) h / 100.0 else h
                            
                            viewModel.updateDimensions(
                                dimensions = Dimensions(
                                    length = lengthM,
                                    width = widthM,
                                    height = heightM
                                ),
                                doors = doors,
                                windows = windows,
                                doorArea = if (useStandardSizes) 2.0 else doorArea.toDoubleOrNull() ?: 2.0,
                                windowArea = if (useStandardSizes) 1.5 else windowArea.toDoubleOrNull() ?: 1.5,
                                marginPercent = margin
                            )
                            onContinue()
                        }
                    },
                    enabled = length.toDoubleOrNull() != null && 
                              width.toDoubleOrNull() != null && 
                              height.toDoubleOrNull() != null,
                    icon = Icons.Default.ArrowForward
                )
            }
        }
    }
}
