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
import com.buildsof.budsde.ui.components.AppCard
import com.buildsof.budsde.ui.components.AppTextField
import com.buildsof.budsde.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    onNavigateBack: () -> Unit
) {
    var selectedCalculator by remember { mutableStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quick Calculator") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = selectedCalculator) {
                Tab(
                    selected = selectedCalculator == 0,
                    onClick = { selectedCalculator = 0 },
                    text = { Text("Area") }
                )
                Tab(
                    selected = selectedCalculator == 1,
                    onClick = { selectedCalculator = 1 },
                    text = { Text("Paint") }
                )
                Tab(
                    selected = selectedCalculator == 2,
                    onClick = { selectedCalculator = 2 },
                    text = { Text("Tiles") }
                )
                Tab(
                    selected = selectedCalculator == 3,
                    onClick = { selectedCalculator = 3 },
                    text = { Text("Flooring") }
                )
            }
            
            when (selectedCalculator) {
                0 -> AreaCalculator()
                1 -> PaintCalculator()
                2 -> TileCalculator()
                3 -> FlooringCalculator()
            }
        }
    }
}

@Composable
fun AreaCalculator() {
    var length by remember { mutableStateOf("") }
    var width by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    
    val area = (length.toDoubleOrNull() ?: 0.0) * (width.toDoubleOrNull() ?: 0.0)
    val wallArea = 2 * ((length.toDoubleOrNull() ?: 0.0) + (width.toDoubleOrNull() ?: 0.0)) * (height.toDoubleOrNull() ?: 0.0)
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader("Room Dimensions")
        }
        
        item {
            AppTextField(
                value = length,
                onValueChange = { length = it },
                label = "Length (m)"
            )
        }
        
        item {
            AppTextField(
                value = width,
                onValueChange = { width = it },
                label = "Width (m)"
            )
        }
        
        item {
            AppTextField(
                value = height,
                onValueChange = { height = it },
                label = "Height (m)"
            )
        }
        
        item {
            SectionHeader("Results")
        }
        
        item {
            AppCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ResultRow("Floor Area:", "${String.format("%.2f", area)} m²")
                    if (height.toDoubleOrNull() != null && height.toDouble() > 0) {
                        ResultRow("Wall Area:", "${String.format("%.2f", wallArea)} m²")
                        ResultRow("Ceiling Area:", "${String.format("%.2f", area)} m²")
                    }
                }
            }
        }
    }
}

@Composable
fun PaintCalculator() {
    var area by remember { mutableStateOf("") }
    var coverage by remember { mutableStateOf("10") }
    var layers by remember { mutableStateOf("2") }
    
    val litersNeeded = (area.toDoubleOrNull() ?: 0.0) / (coverage.toDoubleOrNull() ?: 10.0) * (layers.toIntOrNull() ?: 2)
    val cans1L = kotlin.math.ceil(litersNeeded)
    val cans5L = kotlin.math.ceil(litersNeeded / 5.0)
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader("Paint Calculator")
        }
        
        item {
            AppTextField(
                value = area,
                onValueChange = { area = it },
                label = "Area to paint (m²)"
            )
        }
        
        item {
            AppTextField(
                value = coverage,
                onValueChange = { coverage = it },
                label = "Coverage (m²/L)"
            )
        }
        
        item {
            AppTextField(
                value = layers,
                onValueChange = { layers = it },
                label = "Number of layers"
            )
        }
        
        item {
            SectionHeader("Results")
        }
        
        item {
            AppCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ResultRow("Paint needed:", "${String.format("%.2f", litersNeeded)} L")
                    ResultRow("1L cans:", "${cans1L.toInt()} cans")
                    ResultRow("5L cans:", "${cans5L.toInt()} cans")
                }
            }
        }
    }
}

@Composable
fun TileCalculator() {
    var area by remember { mutableStateOf("") }
    var tileWidth by remember { mutableStateOf("30") }
    var tileHeight by remember { mutableStateOf("30") }
    var margin by remember { mutableStateOf("10") }
    
    val adjustedArea = (area.toDoubleOrNull() ?: 0.0) * (1 + (margin.toIntOrNull() ?: 10) / 100.0)
    val tileArea = (tileWidth.toDoubleOrNull() ?: 30.0) * (tileHeight.toDoubleOrNull() ?: 30.0) / 10000.0
    val tilesNeeded = kotlin.math.ceil(adjustedArea / tileArea).toInt()
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader("Tile Calculator")
        }
        
        item {
            AppTextField(
                value = area,
                onValueChange = { area = it },
                label = "Area (m²)"
            )
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppTextField(
                    value = tileWidth,
                    onValueChange = { tileWidth = it },
                    label = "Tile width (cm)",
                    modifier = Modifier.weight(1f)
                )
                AppTextField(
                    value = tileHeight,
                    onValueChange = { tileHeight = it },
                    label = "Tile height (cm)",
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        item {
            AppTextField(
                value = margin,
                onValueChange = { margin = it },
                label = "Margin (%)"
            )
        }
        
        item {
            SectionHeader("Results")
        }
        
        item {
            AppCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ResultRow("Area with margin:", "${String.format("%.2f", adjustedArea)} m²")
                    ResultRow("Tiles needed:", "$tilesNeeded pcs")
                    ResultRow("Tile adhesive:", "${String.format("%.1f", adjustedArea * 5)} kg (approx)")
                    ResultRow("Grout:", "${String.format("%.1f", adjustedArea * 0.5)} kg (approx)")
                }
            }
        }
    }
}

@Composable
fun FlooringCalculator() {
    var area by remember { mutableStateOf("") }
    var margin by remember { mutableStateOf("10") }
    var underlayment by remember { mutableStateOf(true) }
    
    val adjustedArea = (area.toDoubleOrNull() ?: 0.0) * (1 + (margin.toIntOrNull() ?: 10) / 100.0)
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader("Flooring Calculator")
        }
        
        item {
            AppTextField(
                value = area,
                onValueChange = { area = it },
                label = "Floor area (m²)"
            )
        }
        
        item {
            AppTextField(
                value = margin,
                onValueChange = { margin = it },
                label = "Margin (%)"
            )
        }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Include underlayment")
                    Switch(
                        checked = underlayment,
                        onCheckedChange = { underlayment = it }
                    )
                }
            }
        }
        
        item {
            SectionHeader("Results")
        }
        
        item {
            AppCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ResultRow("Flooring needed:", "${String.format("%.2f", adjustedArea)} m²")
                    if (underlayment) {
                        ResultRow("Underlayment:", "${String.format("%.2f", adjustedArea)} m²")
                    }
                    ResultRow("Baseboard:", "${String.format("%.1f", 2 * kotlin.math.sqrt(area.toDoubleOrNull() ?: 0.0) * 2)} m (approx)")
                }
            }
        }
    }
}

@Composable
fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
