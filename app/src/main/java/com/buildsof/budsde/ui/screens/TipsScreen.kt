package com.buildsof.budsde.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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

data class Tip(
    val title: String,
    val description: String,
    val category: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipsScreen(
    onNavigateBack: () -> Unit
) {
    val tips = remember {
        listOf(
            Tip(
                "Always add 10-15% extra materials",
                "Account for cuts, mistakes, and future repairs. This buffer ensures you don't run out of materials mid-project.",
                "General"
            ),
            Tip(
                "Buy primer before paint",
                "Proper preparation ensures better coverage and longer-lasting results. Never skip the primer step.",
                "Painting"
            ),
            Tip(
                "Measure twice, cut once",
                "Take accurate measurements and double-check before cutting materials. This saves time and money.",
                "General"
            ),
            Tip(
                "Start with the ceiling",
                "Always work from top to bottom. Paint the ceiling first, then walls, and finally the floor.",
                "Painting"
            ),
            Tip(
                "Use quality tools",
                "Invest in good brushes, rollers, and cutting tools. They make the job easier and produce better results.",
                "Tools"
            ),
            Tip(
                "Plan for ventilation",
                "Ensure proper air circulation, especially when painting or working with adhesives. Open windows and use fans.",
                "Safety"
            ),
            Tip(
                "Check for level surfaces",
                "Use a level when installing tiles, hanging wallpaper, or laying flooring. Uneven surfaces lead to poor results.",
                "Installation"
            ),
            Tip(
                "Mix paint from multiple cans",
                "Combine paint from several cans into one large bucket to ensure color consistency throughout the room.",
                "Painting"
            ),
            Tip(
                "Test tile layout first",
                "Lay out tiles without adhesive to plan the pattern and minimize cuts at edges.",
                "Tiling"
            ),
            Tip(
                "Clean as you go",
                "Keep the workspace tidy and clean tools immediately after use. Dried paint and adhesive are hard to remove.",
                "General"
            ),
            Tip(
                "Acclimate materials",
                "Let flooring and other materials sit in the room for 48 hours before installation to adjust to temperature and humidity.",
                "Flooring"
            ),
            Tip(
                "Use painter's tape properly",
                "Apply tape to clean, dry surfaces and remove it while paint is still slightly wet for clean lines.",
                "Painting"
            ),
            Tip(
                "Check for moisture",
                "Test walls and floors for moisture before installing tiles, wallpaper, or flooring to prevent future damage.",
                "Installation"
            ),
            Tip(
                "Buy all materials at once",
                "Purchase materials from the same batch to ensure color and pattern consistency, especially for tiles and wallpaper.",
                "Shopping"
            ),
            Tip(
                "Keep receipts and labels",
                "Save all product information for warranty purposes and future touch-ups or repairs.",
                "General"
            )
        )
    }
    
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "General", "Painting", "Tiling", "Flooring", "Tools", "Safety", "Installation", "Shopping")
    
    val filteredTips = if (selectedCategory == "All") {
        tips
    } else {
        tips.filter { it.category == selectedCategory }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pro Tips") },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Renovation Tips & Best Practices",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Learn from professionals",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            item {
                ScrollableChipRow(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
            }
            
            items(filteredTips) { tip ->
                TipCard(tip)
            }
        }
    }
}

@Composable
fun ScrollableChipRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

@Composable
fun TipCard(tip: Tip) {
    AppCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(32.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tip.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tip.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = tip.category,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}
