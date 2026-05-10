package com.example.a212268_nazatulaini_lab1

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private enum class FoodScreen { NONE, CONFIRM, RESERVED }

data class FoodItem(
    val name: String,
    val originalPrice: Double,
    val discountPercent: Int,
    val seller: String,
    val distance: String,
    val expiresIn: String,
    val description: String,
    val quantity: Int
)

fun getFoodItemData(name: String): FoodItem {
    return when (name) {
        "Apple" -> FoodItem(name, 5.50, 40, "Siti's Kitchen", "1.2km", "2 days", "Fresh red apples from local farm. Slightly overripe but perfect for juicing or eating.", 8)
        "Bread" -> FoodItem(name, 3.80, 50, "Ahmad's Bakery", "0.8km", "1 day", "Freshly baked white bread. Surplus from today's batch — still soft and delicious!", 3)
        "Milk" -> FoodItem(name, 6.20, 30, "Raj's Grocery", "2.1km", "3 days", "Full cream fresh milk. Nearing best-before date but perfectly good to drink.", 5)
        "Cake" -> FoodItem(name, 18.00, 55, "Lin's Bakehouse", "3.5km", "1 day", "Leftover birthday cake — chocolate sponge with buttercream. Great for celebrations!", 1)
        "Banana" -> FoodItem(name, 4.00, 35, "Uncle Chong's Stall", "0.5km", "2 days", "Ripe bananas — ideal for smoothies, banana bread, or eating as-is.", 12)
        "Pizza" -> FoodItem(name, 22.00, 45, "Pizza Corner", "1.9km", "Today", "Leftover whole pizzas from lunch service. Reheat and enjoy!", 2)
        else -> FoodItem(name, 10.00, 30, "Local Seller", "1.0km", "2 days", "Surplus food item available at a reduced price.", 5)
    }
}

@Composable
fun FoodDetailScreen(
    itemName: String,
    onBack: () -> Unit,
    onMessageOwner: (String, String) -> Unit = { _, _ -> },
    onHomeClick: () -> Unit = {},
    chatViewModel: ChatViewModel = viewModel(),
    viewModel: ReServeViewModel = viewModel()
) {
    val userItem = viewModel.getUserListedItem(itemName)
    val item = if (userItem != null) {
        FoodItem(
            name            = userItem.name,
            originalPrice   = userItem.originalPrice,
            discountPercent = userItem.discountPercent,
            seller          = userItem.sellerName,
            distance        = userItem.location,
            expiresIn       = userItem.expiresIn,
            description     = userItem.description,
            quantity        = userItem.quantity
        )
    } else {
        getFoodItemData(itemName)
    }
    val discountedPrice = item.originalPrice * (1 - item.discountPercent / 100.0)
    var quantity by remember { mutableIntStateOf(1) }
    var currentScreen by remember { mutableStateOf(FoodScreen.NONE) }
    var cartMessage by remember { mutableStateOf("") }

    val remainingStock = viewModel.getRemainingStock(itemName)
    val isSoldOut = viewModel.isSoldOut(itemName)

// Clamp quantity if it exceeds remaining stock
    if (quantity > remainingStock && remainingStock > 0) quantity = remainingStock
    Box(modifier = Modifier.fillMaxSize()) {

        // ── 1. Background ──────────────────────────────────────────────
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
        Scaffold(                                  // ← add
            containerColor = Color.Transparent,
            bottomBar = {
                CustomBottomNavigation(
                    onHomeClick = onHomeClick,
                    onSearchClick = onHomeClick,
                    onEmailClick = onHomeClick,
                    onAddClick = onHomeClick
                )
            }
        ){ innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                Image(
                    painter = painterResource(id = getItemImage(itemName)),
                    contentDescription = itemName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.4f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                ),
                                startY = 0f
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.error,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "-${item.discountPercent}%",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }
                }

                Text(
                    text = itemName,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp
                    )
                )
            }

            // Info Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-20).dp),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 24.dp, start = 24.dp, end = 24.dp)
                        .padding(bottom = innerPadding.calculateBottomPadding() + 24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Pricing
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Discounted Price",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "RM %.2f".format(discountedPrice),
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "RM %.2f".format(item.originalPrice),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        textDecoration = TextDecoration.LineThrough,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier.clip(RoundedCornerShape(20.dp)),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)
                        ) {
                            Text(
                                "Expires: ${item.expiresIn}",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Seller Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AccountBox,
                                null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Local Seller",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    item.seller,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Icon(
                                Icons.Default.LocationOn,
                                null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                item.distance,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            chatViewModel.startConversation(item.seller, itemName, getItemImage(itemName))
                            onMessageOwner(item.seller, itemName)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Email, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Message Owner")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "About this item",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        item.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        SuggestionChip(
                            onClick = { },
                            label = { Text("${item.quantity} units left") },
                            icon = { Icon(Icons.Default.Info, null, modifier = Modifier.size(16.dp)) }
                        )
                        SuggestionChip(
                            onClick = { },
                            label = { Text("Quality Assured") },
                            icon = { Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(16.dp)) }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(24.dp))

                    // Quantity
                    Text(
                        "Select Quantity",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedIconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    null,
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                "$quantity",
                                modifier = Modifier.padding(horizontal = 16.dp),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            OutlinedIconButton(
                                onClick = { if (quantity < remainingStock) quantity++ },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    null,
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        Text(
                            if (isSoldOut) "Sold Out" else "Available: $remainingStock",
                            color = if (isSoldOut) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.addToCart(itemName, quantity)
                                cartMessage = "Added to cart!"
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.ShoppingCart, null)
                            Spacer(Modifier.width(8.dp))
                            Text("To Cart", fontSize = 14.sp)
                        }

                        Button(
                            onClick = { currentScreen = FoodScreen.CONFIRM },
                            enabled = !isSoldOut,
                            modifier = Modifier.weight(1.5f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSoldOut) Color.Gray
                                else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                if (isSoldOut) "Sold Out" else "Reserve",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    if (cartMessage.isNotEmpty()) {
                        Text(
                            cartMessage,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }

        // ── 3. Full-Screen CONFIRM Overlay ─────────────────────────────
        AnimatedVisibility(
            visible = currentScreen == FoodScreen.CONFIRM,
            enter = fadeIn(tween(250)) + slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(380, easing = FastOutSlowInEasing)
            ),
            exit = fadeOut(tween(200)) + slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(280)
            )
        ) {
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
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.80f))
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Item image thumbnail
                    Surface(
                        modifier = Modifier.size(110.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = Color.Transparent
                    ) {
                        Image(
                            painter = painterResource(id = getItemImage(itemName)),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        "Confirm Reservation",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Are you sure you want to reserve",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        "$quantity × $itemName",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        "for RM %.2f?".format(discountedPrice * quantity),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // Order summary card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.13f)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Item", color = Color.White.copy(alpha = 0.70f), fontSize = 14.sp)
                                Text(itemName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Quantity", color = Color.White.copy(alpha = 0.70f), fontSize = 14.sp)
                                Text("$quantity unit(s)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Price each", color = Color.White.copy(alpha = 0.70f), fontSize = 14.sp)
                                Text("RM %.2f".format(discountedPrice), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            HorizontalDivider(color = Color.White.copy(alpha = 0.20f))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(
                                    "RM %.2f".format(discountedPrice * quantity),
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    // Confirm button
                    Button(
                        onClick = {
                            viewModel.reserveFoodItem(itemName, quantity)
                            currentScreen = FoodScreen.RESERVED },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            "Yes, Reserve Now",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    TextButton(onClick = { currentScreen = FoodScreen.NONE }) {
                        Text(
                            "Cancel",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // ── 4. Full-Screen RESERVED Success Overlay ────────────────────
        AnimatedVisibility(
            visible = currentScreen == FoodScreen.RESERVED,
            enter = fadeIn(tween(250)) + slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(380, easing = FastOutSlowInEasing)
            ),
            exit = fadeOut(tween(200)) + slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(280)
            )
        ) {
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
                        .background(Color(0xFF1B5E20).copy(alpha = 0.92f))
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Surface(
                            modifier = Modifier.size(130.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.10f)
                        ) {}
                        Surface(
                            modifier = Modifier.size(96.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.18f)
                        ) {}
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(64.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    Text(
                        "Reserved!",
                        style = MaterialTheme.typography.displaySmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        "$quantity × $itemName",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        "RM %.2f".format(discountedPrice * quantity),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.13f)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            ConfirmationInfoRow(
                                Icons.Default.LocationOn,
                                "Pick up from ${item.seller}"
                            )
                            ConfirmationInfoRow(Icons.Default.DateRange, "Collect within 2 hours")
                            ConfirmationInfoRow(Icons.Default.Info, "Show this screen at pickup")
                        }
                    }
                }
                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text(
                            "Back to Listings",
                            color = Color(0xFF1B5E20),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    TextButton(onClick = { currentScreen = FoodScreen.NONE }) {
                        Text(
                            "View Item Again",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfirmationInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color.White.copy(alpha = 0.85f), modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(text, color = Color.White.copy(alpha = 0.90f), fontSize = 14.sp)
    }
}