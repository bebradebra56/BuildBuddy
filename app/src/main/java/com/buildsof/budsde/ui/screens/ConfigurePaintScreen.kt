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
import com.buildsof.budsde.data.PaintType
import com.buildsof.budsde.data.WorkConfig
import com.buildsof.budsde.ui.components.*
import com.buildsof.budsde.viewmodel.ConfigurePaintViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurePaintScreen(
    projectId: String,
    roomId: String,
    workItemId: String,
    viewModel: ConfigurePaintViewModel,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(projectId, roomId, workItemId) {
        viewModel.loadData(projectId, roomId, workItemId)
    }
    
    val workItem = viewModel.workItem
    val existingConfig = workItem?.config as? WorkConfig.PaintConfig
    
    var paintType by remember { mutableStateOf(existingConfig?.paintType ?: PaintType.MATTE) }
    var coverage by remember { mutableStateOf(existingConfig?.coverage?.toString() ?: "10.0") }
    var layers by remember { mutableStateOf(existingConfig?.layers ?: 2) }
    var brand by remember { mutableStateOf(existingConfig?.brand ?: "Generic") }
    var pricePerLiter by remember { mutableStateOf(existingConfig?.pricePerLiter?.toString() ?: "15.0") }
    var includeRoller by remember { mutableStateOf(existingConfig?.includeRoller ?: true) }
    var includeBrush by remember { mutableStateOf(existingConfig?.includeBrush ?: true) }
    var includeTray by remember { mutableStateOf(existingConfig?.includeTray ?: true) }
    
    LaunchedEffect(existingConfig) {
        existingConfig?.let { config ->
            paintType = config.paintType
            coverage = config.coverage.toString()
            layers = config.layers
            brand = config.brand
            pricePerLiter = config.pricePerLiter.toString()
            includeRoller = config.includeRoller
            includeBrush = config.includeBrush
            includeTray = config.includeTray
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configure Paint") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val config = WorkConfig.PaintConfig(
                                paintType = paintType,
                                coverage = coverage.toDoubleOrNull() ?: 10.0,
                                layers = layers,
                                brand = brand,
                                pricePerLiter = pricePerLiter.toDoubleOrNull() ?: 15.0,
                                includeRoller = includeRoller,
                                includeBrush = includeBrush,
                                includeTray = includeTray
                            )
                            viewModel.savePaintConfig(config)
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
                SectionHeader("Paint Type")
            }
            
            item {
                ChipSelector(
                    options = PaintType.values().map { it.name },
                    selectedOption = paintType.name,
                    onOptionSelected = { selected ->
                        paintType = PaintType.valueOf(selected)
                    }
                )
            }
            
            item {
                SectionHeader("Coverage & Layers")
            }
            
            item {
                AppTextField(
                    value = coverage,
                    onValueChange = { coverage = it },
                    label = "Coverage (m² per liter)"
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Number of Layers", fontWeight = FontWeight.Medium)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(1, 2, 3).forEach { count ->
                                PercentageButton(
                                    percent = count,
                                    isSelected = layers == count,
                                    onClick = { layers = count },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
            
            item {
                SectionHeader("Brand & Price")
            }
            
            item {
                AppTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = "Brand"
                )
            }
            
            item {
                AppTextField(
                    value = pricePerLiter,
                    onValueChange = { pricePerLiter = it },
                    label = "Price per Liter"
                )
            }
            
            item {
                SectionHeader("Tools to Include")
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
                            Text("Roller")
                            Checkbox(
                                checked = includeRoller,
                                onCheckedChange = { includeRoller = it }
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Brush")
                            Checkbox(
                                checked = includeBrush,
                                onCheckedChange = { includeBrush = it }
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Paint Tray")
                            Checkbox(
                                checked = includeTray,
                                onCheckedChange = { includeTray = it }
                            )
                        }
                    }
                }
            }
        }
    }
}
