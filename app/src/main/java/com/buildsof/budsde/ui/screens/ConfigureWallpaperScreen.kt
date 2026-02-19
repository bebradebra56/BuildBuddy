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
import com.buildsof.budsde.data.GlueType
import com.buildsof.budsde.data.WallpaperType
import com.buildsof.budsde.data.WorkConfig
import com.buildsof.budsde.ui.components.*
import com.buildsof.budsde.viewmodel.ConfigureWallpaperViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigureWallpaperScreen(
    projectId: String,
    roomId: String,
    workItemId: String,
    viewModel: ConfigureWallpaperViewModel,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(projectId, roomId, workItemId) {
        viewModel.loadData(projectId, roomId, workItemId)
    }
    
    val workItem = viewModel.workItem
    val existingConfig = workItem?.config as? WorkConfig.WallpaperConfig
    
    var wallpaperType by remember { mutableStateOf(existingConfig?.type ?: WallpaperType.VINYL) }
    var rollWidth by remember { mutableStateOf(existingConfig?.rollWidth?.toString() ?: "0.53") }
    var rollLength by remember { mutableStateOf(existingConfig?.rollLength?.toString() ?: "10.05") }
    var hasPattern by remember { mutableStateOf(existingConfig?.hasPattern ?: false) }
    var patternRepeat by remember { mutableStateOf(existingConfig?.patternRepeat?.toString() ?: "0") }
    var glueType by remember { mutableStateOf(existingConfig?.glueType ?: GlueType.UNIVERSAL) }
    var pricePerRoll by remember { mutableStateOf(existingConfig?.pricePerRoll?.toString() ?: "25.0") }
    var includeCorners by remember { mutableStateOf(existingConfig?.includeCorners ?: false) }
    var includeBaseboard by remember { mutableStateOf(existingConfig?.includeBaseboard ?: false) }
    
    LaunchedEffect(existingConfig) {
        existingConfig?.let { config ->
            wallpaperType = config.type
            rollWidth = config.rollWidth.toString()
            rollLength = config.rollLength.toString()
            hasPattern = config.hasPattern
            patternRepeat = config.patternRepeat.toString()
            glueType = config.glueType
            pricePerRoll = config.pricePerRoll.toString()
            includeCorners = config.includeCorners
            includeBaseboard = config.includeBaseboard
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configure Wallpaper") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val config = WorkConfig.WallpaperConfig(
                                type = wallpaperType,
                                rollWidth = rollWidth.toDoubleOrNull() ?: 0.53,
                                rollLength = rollLength.toDoubleOrNull() ?: 10.05,
                                hasPattern = hasPattern,
                                patternRepeat = patternRepeat.toIntOrNull() ?: 0,
                                glueType = glueType,
                                pricePerRoll = pricePerRoll.toDoubleOrNull() ?: 25.0,
                                includeCorners = includeCorners,
                                includeBaseboard = includeBaseboard
                            )
                            viewModel.saveWallpaperConfig(config)
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
                SectionHeader("Wallpaper Type")
            }
            
            item {
                ChipSelector(
                    options = WallpaperType.values().map { it.name },
                    selectedOption = wallpaperType.name,
                    onOptionSelected = { selected ->
                        wallpaperType = WallpaperType.valueOf(selected)
                    }
                )
            }
            
            item {
                SectionHeader("Roll Dimensions")
            }
            
            item {
                AppTextField(
                    value = rollWidth,
                    onValueChange = { rollWidth = it },
                    label = "Roll Width (meters)"
                )
            }
            
            item {
                AppTextField(
                    value = rollLength,
                    onValueChange = { rollLength = it },
                    label = "Roll Length (meters)"
                )
            }
            
            item {
                SectionHeader("Pattern")
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
                            Text("Has Pattern Repeat")
                            Switch(
                                checked = hasPattern,
                                onCheckedChange = { hasPattern = it }
                            )
                        }
                        
                        if (hasPattern) {
                            AppTextField(
                                value = patternRepeat,
                                onValueChange = { patternRepeat = it },
                                label = "Pattern Repeat (cm)"
                            )
                        }
                    }
                }
            }
            
            item {
                SectionHeader("Glue Type")
            }
            
            item {
                ChipSelector(
                    options = GlueType.values().map { it.name },
                    selectedOption = glueType.name,
                    onOptionSelected = { selected ->
                        glueType = GlueType.valueOf(selected)
                    }
                )
            }
            
            item {
                SectionHeader("Price")
            }
            
            item {
                AppTextField(
                    value = pricePerRoll,
                    onValueChange = { pricePerRoll = it },
                    label = "Price per Roll"
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
                            Text("Include Corner Trims")
                            Checkbox(
                                checked = includeCorners,
                                onCheckedChange = { includeCorners = it }
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
