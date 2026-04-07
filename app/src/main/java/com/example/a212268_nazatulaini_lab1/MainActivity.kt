package com.example.a212268_nazatulaini_lab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReServeApp()
        }
    }
}

@Composable
fun ReServeApp() {
    // --- STATES ---
    var selectedFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchMode by remember { mutableStateOf(false) } // Toggles between Home and Search

    // --- DATA ---
    val allFood = listOf("Apple", "Bread", "Milk", "Cake", "Banana", "Pizza")
    val allNonFood = listOf("Guitar", "Trampoline", "Plant Pot", "Chair", "Table", "Books")

    // Filter logic for the Search Screen
    val filteredResults = (allFood + allNonFood).filter {
        it.contains(searchQuery, ignoreCase = true)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.wallpaper),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)))

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                CustomBottomNavigation(
                    onHomeClick = { isSearchMode = false },
                    onSearchClick = { isSearchMode = true }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                if (isSearchMode) {
                    // --- SEARCH SCREEN CONTENT ---
                    Text("Search Items", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))

                    SearchBar(query = searchQuery, onQueryChange = { searchQuery = it })

                    Spacer(modifier = Modifier.height(20.dp))
                    Text("Found ${filteredResults.size} items", color = Color.LightGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    filteredResults.forEach { item ->
                        FullWidthItemCard(item)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                } else {
                    // --- HOME SCREEN CONTENT ---
                    HeaderSection()
                    Spacer(modifier = Modifier.height(20.dp))

                    FilterSection(selectedFilter) { selectedFilter = it }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (selectedFilter == "All") {
                        PromotionSection()
                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalRow("Food Items", allFood)
                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalRow("Non-food Items", allNonFood)
                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalRow("Going Soon", listOf("Bread", "Milk"))
                    } else {
                        // Vertical view for Food or Non-food
                        val itemsToShow = if (selectedFilter == "Food") allFood else allNonFood
                        Text("Category: $selectedFilter", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        itemsToShow.forEach { item ->
                            FullWidthItemCard(item)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Type to search...", color = Color.Gray) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(alpha = 0.9f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.9f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(28.dp),
        singleLine = true
    )
}

@Composable
fun HorizontalRow(title: String, items: List<String>) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
            Text("All >", color = Color.LightGray, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(items) { name ->
                SmallItemCard(name)
            }
        }
    }
}

@Composable
fun SmallItemCard(name: String) {
    Card(
        modifier = Modifier.width(150.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
    ) {
        Column {
            Box(modifier = Modifier.height(100.dp).fillMaxWidth().background(Color.LightGray))
            Column(modifier = Modifier.padding(8.dp)) {
                Text(name, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("21.6km", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun FullWidthItemCard(name: String) {
    Card(
        modifier = Modifier.fillMaxWidth().height(180.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
    ) {
        Column {
            Box(modifier = Modifier.weight(1f).fillMaxWidth().background(Color.Gray))
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                Icon(Icons.Default.ArrowForward, null, tint = Color(0xFF673AB7))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSection(selected: String, onSelect: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("All", "Food", "Non-food").forEach { tag ->
            FilterChip(
                selected = selected == tag,
                onClick = { onSelect(tag) },
                label = { Text(tag) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color.White,
                    labelColor = if (selected == tag) Color.Black else Color.White
                )
            )
        }
    }
}

@Composable
fun HeaderSection() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text("Listings within 5km", color = Color.LightGray, fontSize = 12.sp)
            Text("Kajang Municipal Council", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
        }
        Icon(Icons.Default.Notifications, null, tint = Color.White)
    }
}

@Composable
fun PromotionSection() {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        item { PromoCard("One person's surplus is another's treasure", Color(0xFFFFE0B2)) }
        item { PromoCard("Eco-friendly sharing!", Color(0xFFE1BEE7)) }
    }
}

@Composable
fun PromoCard(text: String, color: Color) {
    Card(modifier = Modifier.size(240.dp, 100.dp), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.8f))) {
        Box(Modifier.padding(16.dp).fillMaxSize()) { Text(text, color = Color.Black, fontWeight = FontWeight.Medium) }
    }
}

@Composable
fun CustomBottomNavigation(onHomeClick: () -> Unit, onSearchClick: () -> Unit) {
    BottomAppBar(containerColor = Color.Black.copy(alpha = 0.8f)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            IconButton(onClick = onHomeClick) { Icon(Icons.Default.Home, null, tint = Color.White) }
            IconButton(onClick = onSearchClick) { Icon(Icons.Default.Search, null, tint = Color.White) }
            FloatingActionButton(onClick = {}, containerColor = Color(0xFF673AB7), shape = RoundedCornerShape(50)) {
                Icon(Icons.Default.Add, null, tint = Color.White)
            }
            IconButton(onClick = {}) { Icon(Icons.Default.Person, null, tint = Color.White) }
            IconButton(onClick = {}) { Icon(Icons.Default.Email, null, tint = Color.White) }
        }
    }
}
