package com.buildsof.budsde.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buildsof.budsde.ui.components.AppCard
import com.buildsof.budsde.ui.components.IconCard
import com.buildsof.budsde.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToCreateProject: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToTemplates: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToShoppingList: () -> Unit,
    onNavigateToTips: () -> Unit,
    onNavigateToCalculator: () -> Unit,
    onNavigateToProjectDetail: (String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val projects by viewModel.projects.collectAsState()
    val settings by viewModel.settings.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Build Buddy",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ) 
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
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
                Text(
                    text = "Quick Actions",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.height(220.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        IconCard(
                            icon = Icons.Default.Add,
                            title = "New Project",
                            onClick = onNavigateToCreateProject
                        )
                    }
                    item {
                        IconCard(
                            icon = Icons.Default.Folder,
                            title = "My Projects",
                            onClick = onNavigateToProjects
                        )
                    }
                    item {
                        IconCard(
                            icon = Icons.Default.ContentCopy,
                            title = "Templates",
                            onClick = onNavigateToTemplates
                        )
                    }
                    item {
                        IconCard(
                            icon = Icons.Default.Lightbulb,
                            title = "Tips",
                            onClick = onNavigateToTips
                        )
                    }
                    item {
                        IconCard(
                            icon = Icons.Default.Calculate,
                            title = "Calculator",
                            onClick = onNavigateToCalculator
                        )
                    }
                    item {
                        IconCard(
                            icon = Icons.Default.Settings,
                            title = "Settings",
                            onClick = onNavigateToSettings
                        )
                    }
                    item {
                        IconCard(
                            icon = Icons.Default.Settings,
                            title = "Settings",
                            onClick = onNavigateToSettings
                        )
                    }
                    item {
                        IconCard(
                            icon = Icons.Default.Info,
                            title = "About",
                            onClick = { /* About screen */ }
                        )
                    }
                }
            }
            
            item {
                Text(
                    text = "Recent Projects",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            if (projects.isEmpty()) {
                item {
                    AppCard {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.HomeWork,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No projects yet",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Create your first renovation project",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(projects.take(3).size) { index ->
                    val project = projects[index]
                    AppCard(
                        onClick = { onNavigateToProjectDetail(project.id) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = project.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (project.address.isNotEmpty()) {
                                    Text(
                                        text = project.address,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text(
                                        text = "${project.rooms.size} rooms",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "${settings.currency.symbol}${String.format("%.0f", project.budget)}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "View project"
                            )
                        }
                    }
                }
            }
            
            item {
                Text(
                    text = "Pro Tips",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            item {
                AppCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(32.dp)
                        )
                        Column {
                            Text(
                                text = "Always add 10-15% extra materials",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Account for cuts, mistakes, and future repairs",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            item {
                AppCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(32.dp)
                        )
                        Column {
                            Text(
                                text = "Buy primer before paint",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Proper preparation ensures better coverage",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
