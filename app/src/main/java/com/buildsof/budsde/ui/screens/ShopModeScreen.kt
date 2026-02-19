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
import com.buildsof.budsde.data.ShoppingItem
import com.buildsof.budsde.ui.components.AppButton
import com.buildsof.budsde.ui.components.AppTextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.buildsof.budsde.viewmodel.ShopModeViewModel
import com.buildsof.budsde.utils.MaterialCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopModeScreen(
    projectId: String,
    viewModel: ShopModeViewModel,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(projectId) {
        viewModel.selectProject(projectId)
    }
    
    val projects by viewModel.projects.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val project = viewModel.selectedProject
    val isLoading = viewModel.isLoading
    
    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    
    if (project == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                Text("Project not found")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onNavigateBack) {
                    Text("Go Back")
                }
            }
        }
        return
    }
    
    val shoppingList = remember(project) {
        MaterialCalculator.calculateShoppingList(project)
    }
    val currency = settings.currency
    var selectedItem by remember { mutableStateOf<ShoppingItem?>(null) }
    var actualPrice by remember { mutableStateOf("") }
    var totalInCart by remember { mutableStateOf(0.0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shop Mode") },
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
        },
        bottomBar = {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Cart Total",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${currency.symbol}${String.format("%.2f", totalInCart)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = "Tap items to quickly add to cart with +1 or +5 buttons",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
            
            items(shoppingList) { item ->
                ShopItemCard(
                    item = item,
                    currency = currency.symbol,
                    onQuickAdd = { quantity ->
                        totalInCart += item.price * quantity
                    },
                    onSelectItem = {
                        selectedItem = item
                        actualPrice = item.price.toString()
                    }
                )
            }
        }
    }
    
    if (selectedItem != null) {
        AlertDialog(
            onDismissRequest = { selectedItem = null },
            title = { Text(selectedItem!!.name) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Enter actual price:")
                    AppTextField(
                        value = actualPrice,
                        onValueChange = { actualPrice = it },
                        label = "Price (${currency.symbol})"
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val price = actualPrice.toDoubleOrNull() ?: selectedItem!!.price
                        totalInCart += price
                        selectedItem = null
                    }
                ) {
                    Text("Add to Cart")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedItem = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ShopItemCard(
    item: ShoppingItem,
    currency: String,
    onQuickAdd: (Double) -> Unit,
    onSelectItem: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = onSelectItem
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${String.format("%.2f", item.quantity)} ${item.unit}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Text(
                    text = "$currency${String.format("%.2f", item.price)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onQuickAdd(1.0) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+1")
                }
                
                OutlinedButton(
                    onClick = { onQuickAdd(5.0) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+5")
                }
                
                OutlinedButton(
                    onClick = onSelectItem,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
