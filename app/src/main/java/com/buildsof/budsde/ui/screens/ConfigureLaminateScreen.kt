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
import com.buildsof.budsde.data.*
import com.buildsof.budsde.ui.components.*
import com.buildsof.budsde.viewmodel.ConfigureLaminateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigureLaminateScreen(
    projectId: String,
    roomId: String,
    workItemId: String,
    viewModel: ConfigureLaminateViewModel,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(projectId, roomId, workItemId) {
        viewModel.loadData(projectId, roomId, workItemId)
    }
    
    val workItem = viewModel.workItem
    val existingConfig = workItem?.config as? WorkConfig.LaminateConfig
    
    var flooringType by remember { mutableStateOf(existingConfig?.type ?: FlooringType.LAMINATE) }
    var classRating by remember { mutableStateOf(existingConfig?.classRating ?: 32) }
    var hasUnderlayment by remember { mutableStateOf(existingConfig?.underlayment != null) }
    var underlaymentThickness by remember { mutableStateOf(existingConfig?.underlayment?.thickness?.toString() ?: "2.0") }
    var underlaymentPrice by remember { mutableStateOf(existingConfig?.underlayment?.pricePerM2?.toString() ?: "3.0") }
    var layout by remember { mutableStateOf(existingConfig?.layout ?: FloorLayout.LENGTHWISE) }
    var pricePerM2 by remember { mutableStateOf(existingConfig?.pricePerM2?.toString() ?: "30.0") }
    var includeThreshold by remember { mutableStateOf(existingConfig?.includeThreshold ?: true) }
    var includeBaseboard by remember { mutableStateOf(existingConfig?.includeBaseboard ?: true) }
    
    LaunchedEffect(existingConfig) {
        existingConfig?.let { config ->
            flooringType = config.type
            classRating = config.classRating
            hasUnderlayment = config.underlayment != null
            underlaymentThickness = config.underlayment?.thickness?.toString() ?: "2.0"
            underlaymentPrice = config.underlayment?.pricePerM2?.toString() ?: "3.0"
            layout = config.layout
            pricePerM2 = config.pricePerM2.toString()
            includeThreshold = config.includeThreshold
            includeBaseboard = config.includeBaseboard
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configure Flooring") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val config = WorkConfig.LaminateConfig(
                                type = flooringType,
                                classRating = classRating,
                                underlayment = if (hasUnderlayment) {
                                    Underlayment(
                                        thickness = underlaymentThickness.toDoubleOrNull() ?: 2.0,
                                        pricePerM2 = underlaymentPrice.toDoubleOrNull() ?: 3.0
                                    )
                                } else null,
                                layout = layout,
                                pricePerM2 = pricePerM2.toDoubleOrNull() ?: 30.0,
                                includeThreshold = includeThreshold,
                                includeBaseboard = includeBaseboard
                            )
                            viewModel.saveLaminateConfig(config)
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
                SectionHeader("Flooring Type")
            }
            
            item {
                ChipSelector(
                    options = FlooringType.values().map { it.name },
                    selectedOption = flooringType.name,
                    onOptionSelected = { selected ->
                        flooringType = FlooringType.valueOf(selected)
                    }
                )
            }
            
            item {
                SectionHeader("Class Rating")
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
                        Text("Durability Class", fontWeight = FontWeight.Medium)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(21, 23, 31, 32, 33).forEach { rating ->
                                FilterChip(
                                    selected = classRating == rating,
                                    onClick = { classRating = rating },
                                    label = { Text("$rating") }
                                )
                            }
                        }
                    }
                }
            }
            
            item {
                SectionHeader("Underlayment")
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
                            Text("Include Underlayment")
                            Switch(
                                checked = hasUnderlayment,
                                onCheckedChange = { hasUnderlayment = it }
                            )
                        }
                        
                        if (hasUnderlayment) {
                            AppTextField(
                                value = underlaymentThickness,
                                onValueChange = { underlaymentThickness = it },
                                label = "Thickness (mm)"
                            )
                            
                            AppTextField(
                                value = underlaymentPrice,
                                onValueChange = { underlaymentPrice = it },
                                label = "Price per m²"
                            )
                        }
                    }
                }
            }
            
            item {
                SectionHeader("Installation Pattern")
            }
            
            item {
                ChipSelector(
                    options = FloorLayout.values().map { it.name },
                    selectedOption = layout.name,
                    onOptionSelected = { selected ->
                        layout = FloorLayout.valueOf(selected)
                    }
                )
            }
            
            item {
                SectionHeader("Flooring Price")
            }
            
            item {
                AppTextField(
                    value = pricePerM2,
                    onValueChange = { pricePerM2 = it },
                    label = "Price per m²"
                )
            }
            
            item {
                SectionHeader("Additional Items")
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
                            Text("Include Threshold")
                            Checkbox(
                                checked = includeThreshold,
                                onCheckedChange = { includeThreshold = it }
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Include Baseboard")
                            Checkbox(
                                checked = includeBaseboard,
                                onCheckedChange = { includeBaseboard = it }
                            )
                        }
                    }
                }
            }
        }
    }
}
