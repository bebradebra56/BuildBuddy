package com.buildsof.budsde.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import com.buildsof.budsde.data.WorkType
import com.buildsof.budsde.ui.components.*
import com.buildsof.budsde.viewmodel.ConfigureSimpleWorkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigureSimpleWorkScreen(
    projectId: String,
    roomId: String,
    workItemId: String,
    workType: WorkType,
    viewModel: ConfigureSimpleWorkViewModel,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(projectId, roomId, workItemId) {
        viewModel.loadData(projectId, roomId, workItemId)
    }
    
    val room = viewModel.room
    val workItem = viewModel.workItem
    
    var pricePerUnit by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    val (title, description, unit) = when (workType) {
        WorkType.BASEBOARD -> Triple(
            "Configure Baseboard",
            "Baseboard will be calculated based on room perimeter",
            "linear meter"
        )
        WorkType.PRIMER -> Triple(
            "Configure Primer",
            "Primer coverage for walls and ceiling",
            "liter"
        )
        WorkType.PUTTY -> Triple(
            "Configure Putty",
            "Wall putty for surface preparation",
            "kg"
        )
        else -> Triple("Configure Work", "Additional work configuration", "unit")
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.saveSimpleConfig()
                            onNavigateBack()
                        }
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = description,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            
            item {
                SectionHeader("Estimated Quantities")
            }
            
            item {
                AppCard {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        room?.let { r ->
                            when (workType) {
                                WorkType.BASEBOARD -> {
                                    val perimeter = 2 * (r.dimensions.length + r.dimensions.width)
                                    InfoRow("Room perimeter:", "${String.format("%.2f", perimeter)} m")
                                    InfoRow("Baseboard needed:", "${String.format("%.2f", perimeter)} m")
                                }
                                WorkType.PRIMER -> {
                                    val wallArea = r.dimensions.wallArea - (r.doors * r.doorArea) - (r.windows * r.windowArea)
                                    val totalArea = wallArea + r.dimensions.ceilingArea
                                    val liters = totalArea / 10.0 // ~10 m² per liter
                                    InfoRow("Total area:", "${String.format("%.2f", totalArea)} m²")
                                    InfoRow("Primer needed:", "${String.format("%.2f", liters)} L (approx)")
                                }
                                WorkType.PUTTY -> {
                                    val wallArea = r.dimensions.wallArea - (r.doors * r.doorArea) - (r.windows * r.windowArea)
                                    val kg = wallArea * 1.2 // ~1.2 kg per m²
                                    InfoRow("Wall area:", "${String.format("%.2f", wallArea)} m²")
                                    InfoRow("Putty needed:", "${String.format("%.2f", kg)} kg (approx)")
                                }
                                else -> {}
                            }
                        }
                    }
                }
            }
            
            item {
                SectionHeader("Pricing (Optional)")
            }
            
            item {
                AppTextField(
                    value = pricePerUnit,
                    onValueChange = { pricePerUnit = it },
                    label = "Price per $unit"
                )
            }
            
            item {
                SectionHeader("Notes (Optional)")
            }
            
            item {
                AppTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "Additional notes",
                    singleLine = false,
                    maxLines = 4
                )
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Column {
                            Text(
                                text = "Pro Tip",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = getTipForWorkType(workType),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getTipForWorkType(workType: WorkType): String {
    return when (workType) {
        WorkType.BASEBOARD -> "Install baseboards after flooring is complete. Use a miter saw for clean corner cuts."
        WorkType.PRIMER -> "Primer is essential for paint adhesion and even coverage. Don't skip this step!"
        WorkType.PUTTY -> "Apply putty in thin layers and sand between coats for a smooth finish."
        else -> "Follow manufacturer instructions for best results."
    }
}
