package com.buildsof.budsde.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.buildsof.budsde.data.Currency
import com.buildsof.budsde.ui.components.AppButton
import com.buildsof.budsde.ui.components.AppTextField
import com.buildsof.budsde.viewmodel.CreateProjectViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectScreen(
    viewModel: CreateProjectViewModel,
    onProjectCreated: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    
    var projectName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(Date()) }
    val selectedCurrency = settings.currency
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
    
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedPhotoUri = uri
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Project") },
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
                    text = "Project Information",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                AppTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    label = "Project Name *"
                )
            }
            
            item {
                AppTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = "Address / Location (Optional)"
                )
            }
            
            item {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    onClick = { showDatePicker = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Start Date",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = dateFormat.format(selectedDate),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Select date",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            item {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    onClick = { photoPickerLauncher.launch("image/*") }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Add Photo (Optional)",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (selectedPhotoUri != null) "Photo selected" else "No photo selected",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Add photo",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        selectedPhotoUri?.let { uri ->
                            Spacer(modifier = Modifier.height(12.dp))
                            Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentDescription = "Selected photo",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
                AppButton(
                    text = "Create Project",
                    onClick = {
                        if (projectName.isNotBlank()) {
                            viewModel.createProject(
                                name = projectName,
                                address = address,
                                startDate = selectedDate,
                                photoUri = selectedPhotoUri?.toString(),
                                onSuccess = { projectId ->
                                    onProjectCreated(projectId)
                                }
                            )
                        }
                    },
                    enabled = projectName.isNotBlank(),
                    icon = Icons.Default.Check
                )
            }
        }
    }
    
    if (showDatePicker) {
        DatePickerDialog(
            initialDate = selectedDate,
            onDateSelected = { date ->
                selectedDate = date
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    initialDate: Date,
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = Calendar.getInstance().apply {
        time = initialDate
    }
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = calendar.timeInMillis
    )
    
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(Date(millis))
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
