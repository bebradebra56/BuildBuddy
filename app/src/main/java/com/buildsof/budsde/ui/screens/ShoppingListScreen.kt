package com.buildsof.budsde.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.buildsof.budsde.data.ShoppingCategory
import com.buildsof.budsde.data.ShoppingItem
import com.buildsof.budsde.ui.components.SectionHeader
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.buildsof.budsde.viewmodel.ShoppingListViewModel
import com.buildsof.budsde.utils.MaterialCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    projectId: String,
    viewModel: ShoppingListViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    
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
    val purchasedItems = remember { mutableStateMapOf<String, Boolean>() }
    val currency = settings.currency
    
    val groupedItems = remember(shoppingList) {
        shoppingList.groupBy { it.category }
    }
    
    fun shareShoppingList() {
        val text = buildString {
            appendLine("Shopping List - ${project.name}")
            appendLine("=" .repeat(40))
            appendLine()
            
            groupedItems.forEach { (category, items) ->
                appendLine(category.name.replace("_", " "))
                appendLine("-".repeat(40))
                items.forEach { item ->
                    appendLine("• ${item.name}")
                    appendLine("  Quantity: ${String.format("%.2f", item.quantity)} ${item.unit}")
                    appendLine("  Price: ${currency.symbol}${String.format("%.2f", item.price)}")
                    appendLine()
                }
                appendLine()
            }
            
            appendLine("Total: ${currency.symbol}${String.format("%.2f", shoppingList.sumOf { it.price })}")
        }
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Shopping List - ${project.name}")
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Share Shopping List"))
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shopping List") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { shareShoppingList() }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                    IconButton(onClick = { shareShoppingList() }) {
                        Icon(Icons.Default.Print, contentDescription = "Print")
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = project.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Items: ${shoppingList.size}")
                            Text(
                                text = "${currency.symbol}${String.format("%.2f", shoppingList.sumOf { it.price })}",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
            
            ShoppingCategory.values().forEach { category ->
                val items = groupedItems[category] ?: emptyList()
                if (items.isNotEmpty()) {
                    item {
                        SectionHeader(category.name.lowercase().split("_")
                            .joinToString(" ") { it.capitalize() })
                    }
                    
                    items(items) { item ->
                        ShoppingItemCard(
                            item = item,
                            currency = currency.symbol,
                            isPurchased = purchasedItems[item.id] ?: false,
                            onPurchasedChange = { purchased ->
                                purchasedItems[item.id] = purchased
                                // TODO: Implement shopping list persistence
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShoppingItemCard(
    item: ShoppingItem,
    currency: String,
    isPurchased: Boolean,
    onPurchasedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isPurchased) 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) 
            else 
                MaterialTheme.colorScheme.surfaceVariant
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
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Checkbox(
                    checked = isPurchased,
                    onCheckedChange = onPurchasedChange
                )
                
                Column {
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
                    if (item.alternatives.isNotEmpty()) {
                        Text(
                            text = "Alternatives available",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$currency${String.format("%.2f", item.price)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
