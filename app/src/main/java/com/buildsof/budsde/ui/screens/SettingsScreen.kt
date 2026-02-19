package com.buildsof.budsde.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buildsof.budsde.data.Currency
import com.buildsof.budsde.data.MeasurementUnit
import com.buildsof.budsde.ui.components.SectionHeader
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.buildsof.budsde.viewmodel.SettingsViewModel
import java.io.BufferedReader
import java.io.InputStreamReader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()
    
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showUnitDialog by remember { mutableStateOf(false) }
    
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    // TODO: Implement export functionality
                    val json = "{}" // Placeholder
                    outputStream.write(json.toByteArray())
                }
                Toast.makeText(context, "Export feature coming soon", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(it)?.use { inputStream ->
                    // TODO: Implement import functionality
                    val json = BufferedReader(InputStreamReader(inputStream)).readText()
                    // Placeholder
                    Toast.makeText(context, "Import feature coming soon", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Import failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
                SectionHeader("Preferences")
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
                        SettingsItem(
                            icon = Icons.Default.Straighten,
                            title = "Measurement Units",
                            value = settings.units.name,
                            onClick = { showUnitDialog = true }
                        )
                        
                        Divider()
                        
                        SettingsItem(
                            icon = Icons.Default.AttachMoney,
                            title = "Currency",
                            value = "${settings.currency.name} (${settings.currency.symbol})",
                            onClick = { showCurrencyDialog = true }
                        )
                        
                        Divider()
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Percent,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Column {
                                    Text(
                                        text = "Default Margin",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${settings.defaultMargin}%",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            item {
                SectionHeader("Appearance")
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DarkMode,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Dark Theme",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Switch(
                            checked = settings.isDarkTheme,
                            onCheckedChange = { isDark ->
                                viewModel.updateSettings(
                                    settings.copy(isDarkTheme = isDark)
                                )
                            }
                        )
                    }
                }
            }
            
            item {
                SectionHeader("Data Management")
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
                        SettingsItem(
                            icon = Icons.Default.Upload,
                            title = "Export Projects",
                            value = "Save to file",
                            onClick = { 
                                exportLauncher.launch("buildbuddy_projects_export.json")
                            }
                        )
                        
                        Divider()
                        
                        SettingsItem(
                            icon = Icons.Default.Download,
                            title = "Import Projects",
                            value = "Load from file",
                            onClick = { 
                                importLauncher.launch("application/json")
                            }
                        )
                    }
                }
            }
            
            item {
                SectionHeader("About")
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
                        SettingsItem(
                            icon = Icons.Default.Star,
                            title = "Privacy Policy",
                            value = "Tap to read",
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://builldbuddy.com/privacy-policy.html"))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
    
    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = { Text("Select Currency") },
            text = {
                Column {
                    Currency.values().forEach { currency ->
                        TextButton(
                            onClick = {
                                viewModel.updateSettings(
                                    settings.copy(currency = currency)
                                )
                                showCurrencyDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${currency.name} (${currency.symbol})",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurrencyDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
    
    if (showUnitDialog) {
        AlertDialog(
            onDismissRequest = { showUnitDialog = false },
            title = { Text("Select Measurement Unit") },
            text = {
                Column {
                    MeasurementUnit.values().forEach { unit ->
                        TextButton(
                            onClick = {
                                viewModel.updateSettings(
                                    settings.copy(units = unit)
                                )
                                showUnitDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = unit.name,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showUnitDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Change"
            )
        }
    }
}
