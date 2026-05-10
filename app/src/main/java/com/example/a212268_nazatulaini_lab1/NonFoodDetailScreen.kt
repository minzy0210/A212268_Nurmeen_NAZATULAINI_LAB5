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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import androidx.lifecycle.viewmodel.compose.viewModel

data class NonFoodItem(
    val name: String,
    val owner: String,
    val distance: String,
    val condition: String,
    val availableUntil: String,
    val description: String,
    val maxBorrowDays: Int,
    val deposit: Double
)

fun getNonFoodItemData(name: String): NonFoodItem {
    return when (name) {
        "Guitar" -> NonFoodItem(name, "Hafiz M.", "2.3km", "Excellent", "Dec 31", "Acoustic guitar, perfect for beginners or casual players. Come pick it up anytime — I just want it to be used!", 14, 50.0)
        "Trampoline" -> NonFoodItem(name, "The Lim Family", "4.1km", "Good", "Nov 30", "Kids trampoline (8ft). Kids have outgrown it. Happy to lend for parties or regular use!", 7, 80.0)
        "Plant Pot" -> NonFoodItem(name, "Kak Ros", "0.9km", "Good", "Ongoing", "Set of 6 ceramic plant pots in various sizes. Great for indoor gardening projects.", 30, 20.0)
        "Chair" -> NonFoodItem(name, "En. Farid", "1.5km", "Fair", "Ongoing", "Folding chairs (×4) — ideal for events, gatherings, or extra seating when guests visit.", 7, 30.0)
        "Table" -> NonFoodItem(name, "Mdm. Wong", "3.2km", "Good", "Jan 15", "Foldable banquet table. Great for events, pasar malam, or temporary workspaces.", 5, 40.0)
        "Books" -> NonFoodItem(name, "Azmi R.", "1.1km", "Good", "Ongoing", "Collection of self-help and fiction books. Free to borrow, just return in good condition!", 21, 0.0)
        else -> NonFoodItem(name, "Community Member", "1.0km", "Good", "Ongoing", "Item available for borrowing from a community member nearby.", 7, 20.0)
    }
}

private enum class NonFoodScreen { NONE, DATE_PICKER, CONFIRMED }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NonFoodDetailScreen(
    itemName: String,
    onBack: () -> Unit,
    onHomeClick: () -> Unit = {},
    onMessageOwner: (String, String) -> Unit = { _, _ -> },
    chatViewModel: ChatViewModel = viewModel(),
    viewModel: ReServeViewModel = viewModel()
) {
    val userItem = viewModel.getUserListedItem(itemName)
    val item = if (userItem != null) {
        NonFoodItem(
            name           = userItem.name,
            owner          = userItem.sellerName,
            distance       = userItem.location,
            condition      = userItem.condition,
            availableUntil = userItem.availableUntil,
            description    = userItem.description,
            maxBorrowDays  = userItem.maxBorrowDays,
            deposit        = userItem.deposit
        )
    } else {
        getNonFoodItemData(itemName)
    }
    val isAlreadyBorrowed = viewModel.isBorrowed(itemName)
    var currentScreen by remember { mutableStateOf(NonFoodScreen.NONE) }

    val today = remember { LocalDate.now() }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var displayedMonth by remember { mutableStateOf(YearMonth.now()) }

    val conditionColor = when (item.condition) {
        "Excellent" -> Color(0xFF2E7D32)
        "Good" -> Color(0xFF1565C0)
        "Fair" -> Color(0xFFE65100)
        else -> Color.Gray
    }

    val borrowDays = if (startDate != null && endDate != null)
        (endDate!!.toEpochDay() - startDate!!.toEpochDay()).toInt() + 1
    else 0

    // ── outermost Box: holds background + all layers ──────────────────
    Box(modifier = Modifier.fillMaxSize()) {

        // ── 1. Background ─────────────────────────────────────────────
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

        // ── 2. Main content inside Scaffold ───────────────────────────
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
                // Hero image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .zIndex(0f)
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
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "BORROW",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = MaterialTheme.colorScheme.onTertiary,
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
                } // end Hero Box

                // Info Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .zIndex(1f),
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(24.dp)
                            .padding(bottom = 32.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Deposit & Condition
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Refundable Deposit",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    if (item.deposit == 0.0) "FREE"
                                    else "RM %.2f".format(item.deposit),
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (item.deposit == 0.0) Color(0xFF2E7D32)
                                        else MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                            Surface(
                                modifier = Modifier.clip(RoundedCornerShape(20.dp)),
                                color = conditionColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    item.condition,
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp, vertical = 6.dp
                                    ),
                                    color = conditionColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Owner card
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
                                    Icons.Default.AccountBox, null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Community Owner",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        item.owner,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Icon(
                                    Icons.Default.LocationOn, null,
                                    tint = MaterialTheme.colorScheme.tertiary,
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
                                chatViewModel.startConversation(
                                    item.owner, itemName, getItemImage(itemName)
                                )
                                onMessageOwner(item.owner, itemName)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
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

                        Spacer(modifier = Modifier.weight(1f))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            SuggestionChip(
                                onClick = { },
                                label = { Text("Max ${item.maxBorrowDays} days") },
                                icon = {
                                    Icon(
                                        Icons.Default.DateRange, null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                            SuggestionChip(
                                onClick = { },
                                label = { Text("Until ${item.availableUntil}") },
                                icon = {
                                    Icon(
                                        Icons.Default.CheckCircle, null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (!isAlreadyBorrowed)
                                    currentScreen = NonFoodScreen.DATE_PICKER
                            },
                            enabled = !isAlreadyBorrowed,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAlreadyBorrowed) Color.Gray
                                else MaterialTheme.colorScheme.tertiary
                            )
                        ) {
                            Icon(
                                if (isAlreadyBorrowed) Icons.Default.Lock
                                else Icons.Default.DateRange,
                                null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (isAlreadyBorrowed) "Currently Borrowed"
                                else "Select Borrow Dates",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    } // end inner Column (scrollable)
                } // end Info Card Surface
            } // end outer Column
        } // end Scaffold

        // ── 3. DATE_PICKER overlay — outside Scaffold ─────────────────
        AnimatedVisibility(
            visible = currentScreen == NonFoodScreen.DATE_PICKER,
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
                        .verticalScroll(rememberScrollState())
                ) {
                    // Top bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                currentScreen = NonFoodScreen.NONE
                                startDate = null
                                endDate = null
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Text(
                            "Select Dates",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    // Calendar card
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            // Month navigation
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = {
                                    displayedMonth = displayedMonth.minusMonths(1)
                                }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.KeyboardArrowLeft, null,
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    displayedMonth.month.getDisplayName(
                                        TextStyle.FULL, Locale.getDefault()
                                    ) + " " + displayedMonth.year,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                IconButton(onClick = {
                                    displayedMonth = displayedMonth.plusMonths(1)
                                }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.KeyboardArrowRight, null,
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            // Day-of-week headers
                            Row(modifier = Modifier.fillMaxWidth()) {
                                listOf("Su","Mo","Tu","We","Th","Fr","Sa").forEach { day ->
                                    Text(
                                        day,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            // Calendar grid
                            val firstDayOfMonth = displayedMonth.atDay(1)
                            val startDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
                            val daysInMonth = displayedMonth.lengthOfMonth()
                            val rows = (startDayOfWeek + daysInMonth + 6) / 7

                            for (row in 0 until rows) {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    for (col in 0 until 7) {
                                        val dayNumber = row * 7 + col - startDayOfWeek + 1
                                        if (dayNumber < 1 || dayNumber > daysInMonth) {
                                            Box(modifier = Modifier.weight(1f).height(40.dp))
                                        } else {
                                            val date = displayedMonth.atDay(dayNumber)
                                            val isPast = date.isBefore(today)
                                            val isStart = date == startDate
                                            val isEnd = date == endDate
                                            val isInRange = startDate != null && endDate != null &&
                                                    date.isAfter(startDate) && date.isBefore(endDate)
                                            val isSelected = isStart || isEnd
                                            val isToday = date == today
                                            val exceedsMax = startDate != null && endDate == null &&
                                                    date.isAfter(
                                                        startDate!!.plusDays(item.maxBorrowDays.toLong() - 1)
                                                    )
                                            val isDisabled = isPast || exceedsMax

                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(40.dp)
                                                    .background(
                                                        color = when {
                                                            isSelected -> MaterialTheme.colorScheme.tertiary
                                                            isInRange -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                                                            else -> Color.Transparent
                                                        },
                                                        shape = when {
                                                            isStart && endDate != null -> RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)
                                                            isEnd -> RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)
                                                            isInRange -> RoundedCornerShape(0.dp)
                                                            isSelected -> CircleShape
                                                            else -> RoundedCornerShape(0.dp)
                                                        }
                                                    )
                                                    .clickable(enabled = !isDisabled) {
                                                        when {
                                                            startDate == null -> startDate = date
                                                            endDate == null && date.isAfter(startDate) -> endDate = date
                                                            endDate == null && date == startDate -> startDate = null
                                                            else -> { startDate = date; endDate = null }
                                                        }
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    "$dayNumber",
                                                    fontSize = 14.sp,
                                                    fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                                    color = when {
                                                        isDisabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                                                        isSelected -> MaterialTheme.colorScheme.onTertiary
                                                        isToday -> MaterialTheme.colorScheme.tertiary
                                                        isInRange -> MaterialTheme.colorScheme.tertiary
                                                        else -> MaterialTheme.colorScheme.onSurface
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } // end Calendar Surface Column
                    } // end Calendar Surface

                    Spacer(Modifier.height(20.dp))

                    // Date range summary
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            val fmt = DateTimeFormatter.ofPattern("d MMM")
                            DateSummaryColumn(
                                label = "Pick-up Date",
                                value = startDate?.format(fmt) ?: "Not set",
                                isSet = startDate != null
                            )
                            VerticalDivider(
                                modifier = Modifier.height(48.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                            DateSummaryColumn(
                                label = "Return Date",
                                value = endDate?.format(fmt) ?: "Not set",
                                isSet = endDate != null
                            )
                            VerticalDivider(
                                modifier = Modifier.height(48.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                            DateSummaryColumn(
                                label = "Duration",
                                value = if (borrowDays > 0)
                                    "$borrowDays day${if (borrowDays > 1) "s" else ""}"
                                else "--",
                                isSet = borrowDays > 0
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // Confirm button
                    Button(
                        onClick = {
                            if (startDate != null && endDate != null) {
                                viewModel.borrowNonFoodItem(itemName)
                                currentScreen = NonFoodScreen.CONFIRMED
                            }
                        },
                        enabled = startDate != null && endDate != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            disabledContainerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f)
                        )
                    ) {
                        Text(
                            if (startDate == null) "Select a start date"
                            else if (endDate == null) "Now select a return date"
                            else "Confirm Dates",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        "Max ${item.maxBorrowDays} days allowed",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 13.sp
                    )

                    Spacer(Modifier.height(16.dp))
                } // end DATE_PICKER Column
            } // end DATE_PICKER Box
        } // end DATE_PICKER AnimatedVisibility

        // ── 4. CONFIRMED overlay — outside Scaffold ───────────────────
        AnimatedVisibility(
            visible = currentScreen == NonFoodScreen.CONFIRMED,
            enter = fadeIn(tween(250)) + slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(380, easing = FastOutSlowInEasing)
            ),
            exit = fadeOut(tween(200)) + slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(280)
            )
        ) {
            val fmt = DateTimeFormatter.ofPattern("d MMM yyyy")

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
                        .background(Color(0xFF0D47A1).copy(alpha = 0.92f))
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

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        "Request Sent!",
                        style = MaterialTheme.typography.displaySmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        itemName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.13f)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            BorrowInfoRow(Icons.Default.DateRange, "Pick-up: ${startDate?.format(fmt) ?: ""}")
                            BorrowInfoRow(Icons.Default.DateRange, "Return by: ${endDate?.format(fmt) ?: ""}")
                            BorrowInfoRow(Icons.Default.Info, "$borrowDays day${if (borrowDays > 1) "s" else ""} borrow period")
                            BorrowInfoRow(Icons.Default.Person, "Owner: ${item.owner} · ${item.distance}")
                            if (item.deposit > 0.0) {
                                BorrowInfoRow(Icons.Default.AccountBox, "Deposit: RM %.2f (refundable)".format(item.deposit))
                            } else {
                                BorrowInfoRow(Icons.Default.CheckCircle, "No deposit required")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    Button(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text(
                            "Back to Listings",
                            color = Color(0xFF0D47A1),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    TextButton(onClick = { currentScreen = NonFoodScreen.NONE }) {
                        Text(
                            "View Item Again",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } // end CONFIRMED AnimatedVisibility

    } // end outermost Box
}

@Composable
private fun DateSummaryColumn(label: String, value: String, isSet: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSet) MaterialTheme.colorScheme.tertiary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun BorrowInfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color.White.copy(alpha = 0.85f), modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(text, color = Color.White.copy(alpha = 0.90f), fontSize = 14.sp)
    }
}