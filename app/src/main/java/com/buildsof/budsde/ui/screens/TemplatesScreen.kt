package com.buildsof.budsde.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.buildsof.budsde.data.Template
import com.buildsof.budsde.ui.components.AppCard
import com.buildsof.budsde.ui.components.AppTextField
import com.buildsof.budsde.viewmodel.TemplatesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatesScreen(
    viewModel: TemplatesViewModel,
    onNavigateBack: () -> Unit
) {
    val templates = viewModel.templates
    var selectedTemplate by remember { mutableStateOf<Template?>(null) }
    var showApplyDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Templates") },
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
                    text = "Ready-Made Configurations",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Quick start with pre-configured work sets",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            items(templates) { template ->
                TemplateCard(
                    template = template,
                    onApply = {
                        selectedTemplate = template
                        showApplyDialog = true
                    }
                )
            }
        }
    }
    
    if (showApplyDialog && selectedTemplate != null) {
        ApplyTemplateDialog(
            template = selectedTemplate!!,
            viewModel = viewModel,
            onDismiss = { 
                showApplyDialog = false
                selectedTemplate = null
            },
            onApply = { projectName ->
                viewModel.createProjectFromTemplate(
                    template = selectedTemplate!!,
                    projectName = projectName,
                    onSuccess = { projectId ->
                        showApplyDialog = false
                        selectedTemplate = null
                        onNavigateBack()
                    }
                )
            }
        )
    }
}

@Composable
fun TemplateCard(
    template: Template,
    onApply: () -> Unit
) {
    AppCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = template.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = template.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Divider(modifier = Modifier.padding(vertical = 4.dp))
            
            Text(
                text = "Included Works:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            template.workItems.forEach { workItem ->
                Text(
                    text = "• ${workItem.type.name.replace("_", " ").lowercase().split(" ").joinToString(" ") { it.capitalize() }}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = onApply,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Use This Template")
            }
        }
    }
}

@Composable
fun ApplyTemplateDialog(
    template: Template,
    viewModel: TemplatesViewModel,
    onDismiss: () -> Unit,
    onApply: (String) -> Unit
) {
    var projectName by remember { mutableStateOf("${template.name} Project") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Project from Template") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("This will create a new project based on: ${template.name}")
                
                AppTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    label = "Project Name"
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (projectName.isNotBlank()) {
                        onApply(projectName)
                    }
                },
                enabled = projectName.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
