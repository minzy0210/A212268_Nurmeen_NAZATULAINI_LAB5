package com.example.a212268_nazatulaini_lab1

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GoingSoonScreen(
    onBack: () -> Unit,
    onItemClick: (String) -> Unit,
    onHomeClick: () -> Unit = {},
    viewModel: ReServeViewModel
) {
    val items = viewModel.getGoingSoon()

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.wallpaper),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.65f))
        )

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                CustomBottomNavigation(
                    onHomeClick = onHomeClick,
                    onSearchClick = onHomeClick,
                    onEmailClick = onHomeClick,
                    onAddClick = onHomeClick
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Top bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFB71C1C).copy(alpha = 0.85f),
                                        Color(0xFFE53935).copy(alpha = 0.6f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp, start = 16.dp, end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "Going Soon",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                            Text(
                                "${items.size} items expiring soon — grab them before they're gone!",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Urgent banner
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFB71C1C).copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = null,
                            tint = Color(0xFFEF9A9A),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "These items expire within 24 hours. Reserve now to avoid food waste!",
                            color = Color(0xFFEF9A9A),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }

                // Items list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 8.dp, top = 8.dp)
                ) {
                    items(items) { item ->
                        GoingSoonItemCard(
                            name = item.name,
                            viewModel = viewModel,
                            onClick = { onItemClick(item.name) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GoingSoonItemCard(
    name: String,
    viewModel: ReServeViewModel,
    onClick: () -> Unit
) {
    val userItem = viewModel.getUserListedItem(name)
    val originalPrice  = userItem?.originalPrice  ?: getFoodItemData(name).originalPrice
    val discountPercent = userItem?.discountPercent ?: getFoodItemData(name).discountPercent
    val expiresIn      = userItem?.expiresIn       ?: getFoodItemData(name).expiresIn
    val seller         = userItem?.sellerName      ?: getFoodItemData(name).seller
    val distance       = userItem?.location        ?: getFoodItemData(name).distance

    val discountedPrice = originalPrice * (1 - discountPercent / 100.0)
    val isSoldOut  = viewModel.isSoldOut(name)
    val remaining  = viewModel.getRemainingStock(name)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isSoldOut) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        )
    ) {
        Row(modifier = Modifier.height(120.dp)) {

            Box(modifier = Modifier.width(120.dp).fillMaxHeight()) {
                Image(
                    painter = painterResource(id = getItemImage(name)),
                    contentDescription = name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    color = Color(0xFFB71C1C),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Expires: $expiresIn",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (isSoldOut) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.55f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.error,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Sold Out",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        name,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        seller,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                       distance,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            "RM %.2f".format(discountedPrice),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "RM %.2f".format(originalPrice),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                    Surface(
                        color = if (isSoldOut) Color.Gray.copy(alpha = 0.15f)
                        else Color(0xFFB71C1C).copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            if (isSoldOut) "Gone" else "$remaining left · discountPercent}%",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = if (isSoldOut) Color.Gray else Color(0xFFB71C1C),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}