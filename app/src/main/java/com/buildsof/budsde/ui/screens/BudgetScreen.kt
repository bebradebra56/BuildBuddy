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
import com.buildsof.budsde.data.ShoppingCategory
import com.buildsof.budsde.ui.components.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.buildsof.budsde.viewmodel.BudgetViewModel
import com.buildsof.budsde.utils.MaterialCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    projectId: String,
    viewModel: BudgetViewModel,
    onContinue: () -> Unit,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(projectId) {
        viewModel.loadProject(projectId)
    }
    
    val project = viewModel.project
    val settings by viewModel.settings.collectAsState()
    val currency = settings.currency
    
    if (project == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    
    var budgetInput by remember { mutableStateOf(project.budget.toString()) }
    var deliveryCost by remember { mutableStateOf("0") }
    var discountPercent by remember { mutableStateOf("0") }
    var taxPercent by remember { mutableStateOf("0") }
    
    LaunchedEffect(project) {
        budgetInput = project.budget.toString()
    }
    
    // Calculate materials and costs
    val shoppingList = remember(project) {
        MaterialCalculator.calculateShoppingList(project)
    }
    
    val materialsCost = remember(shoppingList) {
        shoppingList
            .filter { it.category == ShoppingCategory.MATERIALS }
            .sumOf { it.price * it.quantity }
    }
    
    val toolsCost = remember(shoppingList) {
        shoppingList
            .filter { it.category == ShoppingCategory.TOOLS }
            .sumOf { it.price * it.quantity }
    }
    
    val consumablesCost = remember(shoppingList) {
        shoppingList
            .filter { it.category == ShoppingCategory.CONSUMABLES }
            .sumOf { it.price * it.quantity }
    }
    
    val subtotal = materialsCost + toolsCost + consumablesCost
    val delivery = deliveryCost.toDoubleOrNull() ?: 0.0
    val discount = subtotal * ((discountPercent.toDoubleOrNull() ?: 0.0) / 100.0)
    val tax = (subtotal - discount) * ((taxPercent.toDoubleOrNull() ?: 0.0) / 100.0)
    val total = subtotal + delivery - discount + tax
    
    val budget = budgetInput.toDoubleOrNull() ?: 0.0
    val isOverBudget = budget > 0 && total > budget
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budget & Pricing") },
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
                SectionHeader("Project Budget")
            }
            
            item {
                AppTextField(
                    value = budgetInput,
                    onValueChange = {
                        budgetInput = it
                        // TODO: Implement budget update
                    },
                    label = "Total Budget (${currency.symbol})"
                )
            }
            
            item {
                SectionHeader("Cost Breakdown")
            }
            
            item {
                AppCard {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoRow(
                            "Materials:",
                            "${currency.symbol}${String.format("%.2f", materialsCost)}"
                        )
                        InfoRow(
                            "Tools:",
                            "${currency.symbol}${String.format("%.2f", toolsCost)}"
                        )
                        InfoRow(
                            "Consumables:",
                            "${currency.symbol}${String.format("%.2f", consumablesCost)}"
                        )
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                        InfoRow(
                            "Subtotal:",
                            "${currency.symbol}${String.format("%.2f", subtotal)}"
                        )
                    }
                }
            }
            
            item {
                SectionHeader("Additional Costs")
            }
            
            item {
                AppTextField(
                    value = deliveryCost,
                    onValueChange = { deliveryCost = it },
                    label = "Delivery Cost (${currency.symbol})"
                )
            }
            
            item {
                AppTextField(
                    value = discountPercent,
                    onValueChange = { discountPercent = it },
                    label = "Discount (%)"
                )
            }
            
            item {
                AppTextField(
                    value = taxPercent,
                    onValueChange = { taxPercent = it },
                    label = "Tax/Fee (%)"
                )
            }
            
            item {
                SectionHeader("Final Total")
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isOverBudget) 
                            MaterialTheme.colorScheme.errorContainer 
                        else 
                            MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (delivery > 0) {
                            InfoRow(
                                "Delivery:",
                                "+${currency.symbol}${String.format("%.2f", delivery)}"
                            )
                        }
                        if (discount > 0) {
                            InfoRow(
                                "Discount:",
                                "-${currency.symbol}${String.format("%.2f", discount)}"
                            )
                        }
                        if (tax > 0) {
                            InfoRow(
                                "Tax:",
                                "+${currency.symbol}${String.format("%.2f", tax)}"
                            )
                        }
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "TOTAL:",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${currency.symbol}${String.format("%.2f", total)}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOverBudget) 
                                    MaterialTheme.colorScheme.error 
                                else 
                                    MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        if (budget > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isOverBudget) "Over Budget:" else "Remaining:",
                                    fontSize = 14.sp,
                                    color = if (isOverBudget) 
                                        MaterialTheme.colorScheme.error 
                                    else 
                                        MaterialTheme.colorScheme.tertiary
                                )
                                Text(
                                    text = "${currency.symbol}${String.format("%.2f", kotlin.math.abs(budget - total))}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isOverBudget) 
                                        MaterialTheme.colorScheme.error 
                                    else 
                                        MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                AppButton(
                    text = "Continue to Shopping List",
                    onClick = onContinue,
                    icon = Icons.Default.ShoppingCart
                )
            }
        }
    }
}
