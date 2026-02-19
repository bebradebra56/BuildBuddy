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
import com.buildsof.budsde.data.*
import com.buildsof.budsde.ui.components.*
import com.buildsof.budsde.viewmodel.ConfigureTileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigureTileScreen(
    projectId: String,
    roomId: String,
    workItemId: String,
    viewModel: ConfigureTileViewModel,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(projectId, roomId, workItemId) {
        viewModel.loadData(projectId, roomId, workItemId)
    }
    
    val workItem = viewModel.workItem
    val existingConfig = workItem?.config as? WorkConfig.TileConfig
    
    var surface by remember { mutableStateOf(existingConfig?.surface ?: TileSurface.FLOOR) }
    var tileWidth by remember { mutableStateOf(existingConfig?.tileWidth?.toString() ?: "30.0") }
    var tileHeight by remember { mutableStateOf(existingConfig?.tileHeight?.toString() ?: "30.0") }
    var layout by remember { mutableStateOf(existingConfig?.layout ?: TileLayout.STRAIGHT) }
    var margin by remember { mutableStateOf(existingConfig?.margin ?: 10) }
    var glueCoverage by remember { mutableStateOf(existingConfig?.glue?.coverageKgPerM2?.toString() ?: "5.0") }
    var gluePrice by remember { mutableStateOf(existingConfig?.glue?.pricePerKg?.toString() ?: "8.0") }
    var groutColor by remember { mutableStateOf(existingConfig?.grout?.color ?: "#FFFFFF") }
    var groutPrice by remember { mutableStateOf(existingConfig?.grout?.pricePerKg?.toString() ?: "10.0") }
    var spacerSize by remember { mutableStateOf(existingConfig?.spacerSize ?: 2) }
    var pricePerM2 by remember { mutableStateOf(existingConfig?.pricePerM2?.toString() ?: "35.0") }
    
    LaunchedEffect(existingConfig) {
        existingConfig?.let { config ->
            surface = config.surface
            tileWidth = config.tileWidth.toString()
            tileHeight = config.tileHeight.toString()
            layout = config.layout
            margin = config.margin
            glueCoverage = config.glue?.coverageKgPerM2?.toString() ?: "5.0"
            gluePrice = config.glue?.pricePerKg?.toString() ?: "8.0"
            groutColor = config.grout?.color ?: "#FFFFFF"
            groutPrice = config.grout?.pricePerKg?.toString() ?: "10.0"
            spacerSize = config.spacerSize
            pricePerM2 = config.pricePerM2.toString()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configure Tiles") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val config = WorkConfig.TileConfig(
                                surface = surface,
                                tileWidth = tileWidth.toDoubleOrNull() ?: 30.0,
                                tileHeight = tileHeight.toDoubleOrNull() ?: 30.0,
                                layout = layout,
                                margin = margin,
                                glue = TileGlue(
                                    coverageKgPerM2 = glueCoverage.toDoubleOrNull() ?: 5.0,
                                    pricePerKg = gluePrice.toDoubleOrNull() ?: 8.0
                                ),
                                grout = TileGrout(
                                    color = groutColor,
                                    pricePerKg = groutPrice.toDoubleOrNull() ?: 10.0
                                ),
                                spacerSize = spacerSize,
                                pricePerM2 = pricePerM2.toDoubleOrNull() ?: 35.0
                            )
                            viewModel.saveTileConfig(config)
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
                SectionHeader("Surface")
            }
            
            item {
                ChipSelector(
                    options = TileSurface.values().map { it.name },
                    selectedOption = surface.name,
                    onOptionSelected = { selected ->
                        surface = TileSurface.valueOf(selected)
                    }
                )
            }
            
            item {
                SectionHeader("Tile Size (cm)")
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppTextField(
                        value = tileWidth,
                        onValueChange = { tileWidth = it },
                        label = "Width",
                        modifier = Modifier.weight(1f)
                    )
                    
                    AppTextField(
                        value = tileHeight,
                        onValueChange = { tileHeight = it },
                        label = "Height",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            item {
                Text(
                    text = "Quick presets:",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("30×30", "60×60", "20×60").forEach { preset ->
                        FilterChip(
                            selected = false,
                            onClick = {
                                val parts = preset.split("×")
                                tileWidth = parts[0]
                                tileHeight = parts[1]
                            },
                            label = { Text(preset) }
                        )
                    }
                }
            }
            
            item {
                SectionHeader("Installation Pattern")
            }
            
            item {
                ChipSelector(
                    options = TileLayout.values().map { it.name },
                    selectedOption = layout.name,
                    onOptionSelected = { selected ->
                        layout = TileLayout.valueOf(selected)
                    }
                )
            }
            
            item {
                SectionHeader("Margin/Buffer")
            }
            
            item {
                PercentageSelector(
                    selectedPercent = margin,
                    onPercentSelected = { margin = it }
                )
            }
            
            item {
                SectionHeader("Tile Adhesive")
            }
            
            item {
                AppTextField(
                    value = glueCoverage,
                    onValueChange = { glueCoverage = it },
                    label = "Coverage (kg per m²)"
                )
            }
            
            item {
                AppTextField(
                    value = gluePrice,
                    onValueChange = { gluePrice = it },
                    label = "Price per kg"
                )
            }
            
            item {
                SectionHeader("Grout")
            }
            
            item {
                AppTextField(
                    value = groutColor,
                    onValueChange = { groutColor = it },
                    label = "Color (hex)"
                )
            }
            
            item {
                AppTextField(
                    value = groutPrice,
                    onValueChange = { groutPrice = it },
                    label = "Price per kg"
                )
            }
            
            item {
                SectionHeader("Spacers")
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
                        Text("Spacer Size (mm)", fontWeight = FontWeight.Medium)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(1, 2, 3, 5).forEach { size ->
                                PercentageButton(
                                    percent = size,
                                    isSelected = spacerSize == size,
                                    onClick = { spacerSize = size },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
            
            item {
                SectionHeader("Tile Price")
            }
            
            item {
                AppTextField(
                    value = pricePerM2,
                    onValueChange = { pricePerM2 = it },
                    label = "Price per m²"
                )
            }
        }
    }
}
