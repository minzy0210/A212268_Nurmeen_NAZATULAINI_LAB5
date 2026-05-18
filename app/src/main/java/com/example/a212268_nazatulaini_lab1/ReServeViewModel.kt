package com.example.a212268_nazatulaini_lab1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.a212268_nazatulaini_lab1.data.ReServeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 5. VIEWMODEL — calls repository methods, exposes StateFlow to the UI.
//    No direct database or DAO imports here.
class ReServeViewModel(
    private val repository: ReServeRepository
) : ViewModel() {

    // ── Static (hardcoded) items — unchanged ─────────────────────────
    private val _items = MutableStateFlow(
        listOf(
            Item("Apple", "Food"), Item("Bread", "Food"), Item("Milk", "Food"),
            Item("Cake", "Food"), Item("Banana", "Food"), Item("Pizza", "Food"),
            Item("Guitar", "Non-food"), Item("Trampoline", "Non-food"),
            Item("Plant Pot", "Non-food"), Item("Chair", "Non-food"),
            Item("Table", "Non-food"), Item("Books", "Non-food")
        )
    )
    val items: StateFlow<List<Item>> = _items

    // ── User-listed items — now from Room via repository ─────────────
    // stateIn() converts the cold Flow from the DB into a hot StateFlow
    // the UI can collect with collectAsStateWithLifecycle()
    val userListedItems: StateFlow<List<UserListedItem>> =
        repository.userListedItems.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // ── Cart items — now persisted in Room ───────────────────────────
    val cartItems: StateFlow<List<CartItem>> =
        repository.cartItems.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // ── Borrow / reservation state (still in-memory; extend if needed) ─
    private val _reservedQuantities = MutableStateFlow<Map<String, Int>>(emptyMap())
    val reservedQuantities: StateFlow<Map<String, Int>> = _reservedQuantities.asStateFlow()

    private val _borrowedItems = MutableStateFlow<Set<String>>(emptySet())
    val borrowedItems: StateFlow<Set<String>> = _borrowedItems.asStateFlow()

    // ── UserListedItem operations ─────────────────────────────────────

    /** Called from AddItemScreen — launches a coroutine to insert into Room */
    fun addUserItem(item: UserListedItem) {
        viewModelScope.launch { repository.addUserItem(item) }
    }

    fun deleteUserItem(item: UserListedItem) {
        viewModelScope.launch { repository.deleteUserItem(item) }
    }

    fun updateUserItem(item: UserListedItem) {
        viewModelScope.launch { repository.updateUserItem(item) }
    }

    // Synchronous helpers (read from the already-loaded StateFlow value)
    fun getUserListedItem(name: String): UserListedItem? =
        userListedItems.value.firstOrNull { it.name == name }

    fun getPhotoUri(name: String): String? =
        userListedItems.value.firstOrNull { it.name == name }?.photoUri

    fun getDistance(name: String): String {
        userListedItems.value.firstOrNull { it.name == name }?.let { return it.location }
        return when (name) {
            "Apple" -> "1.2km"; "Bread" -> "0.8km"; "Milk" -> "2.1km"
            "Cake" -> "3.5km"; "Banana" -> "0.5km"; "Pizza" -> "1.9km"
            "Guitar" -> "2.3km"; "Trampoline" -> "4.1km"; "Plant Pot" -> "0.9km"
            "Chair" -> "1.5km"; "Table" -> "3.2km"; "Books" -> "1.1km"
            else -> "N/A"
        }
    }

    // ── Cart operations ───────────────────────────────────────────────

    fun addToCart(itemName: String, quantity: Int = 1) {
        viewModelScope.launch {
            val foodItem = getFoodItemData(itemName)
            val discountedPrice = foodItem.originalPrice * (1 - foodItem.discountPercent / 100.0)
            val imageRes = getItemImage(itemName)

            val existing = cartItems.value.find { it.name == itemName }
            if (existing != null) {
                repository.updateCartItem(existing.copy(quantity = existing.quantity + quantity))
            } else {
                repository.addToCart(
                    CartItem(name = itemName, imageRes = imageRes, price = discountedPrice, quantity = quantity)
                )
            }
        }
    }

    fun removeFromCart(itemName: String) {
        viewModelScope.launch { repository.removeFromCart(itemName) }
    }

    fun clearCart() {
        viewModelScope.launch { repository.clearCart() }
    }

    fun decrementCart(itemName: String) {
        viewModelScope.launch {
            val existing = cartItems.value.find { it.name == itemName } ?: return@launch
            if (existing.quantity > 1) {
                repository.updateCartItem(existing.copy(quantity = existing.quantity - 1))
            } else {
                repository.removeFromCart(itemName)
            }
        }
    }

    fun getCartTotal(): Double = cartItems.value.sumOf { it.price * it.quantity }

    // ── Reservation / borrow (in-memory) ─────────────────────────────

    fun reserveFoodItem(itemName: String, quantity: Int) {
        _reservedQuantities.update { current ->
            current.toMutableMap().also { it[itemName] = (it[itemName] ?: 0) + quantity }
        }
    }

    fun borrowNonFoodItem(itemName: String) {
        _borrowedItems.update { it + itemName }
    }

    fun getRemainingStock(itemName: String): Int {
        getUserListedItem(itemName)?.let { userItem ->
            if (userItem.category == "Food") {
                val reserved = _reservedQuantities.value[itemName] ?: 0
                return (userItem.quantity - reserved).coerceAtLeast(0)
            }
        }
        val originalQty = getFoodItemData(itemName).quantity
        val reserved = _reservedQuantities.value[itemName] ?: 0
        return (originalQty - reserved).coerceAtLeast(0)
    }

    fun isSoldOut(itemName: String) = getRemainingStock(itemName) <= 0
    fun isBorrowed(itemName: String) = itemName in _borrowedItems.value

    // ── Category helpers ──────────────────────────────────────────────

    fun getFoodItems(): List<Item> {
        val base = _items.value.filter { it.category == "Food" }
        val userFood = userListedItems.value
            .filter { it.category.equals("Food", ignoreCase = true) }
            .map { Item(it.name, "Food") }
        return base + userFood
    }

    fun getNonFoodItems(): List<Item> {
        val base = _items.value.filter { it.category == "Non-food" }
        val userNonFood = userListedItems.value
            .filter { it.category.equals("Non-food", ignoreCase = true) ||
                    it.category.equals("Non-Food", ignoreCase = true) }
            .map { Item(it.name, "Non-food") }
        return base + userNonFood
    }

    fun searchItems(query: String): List<Item> {
        if (query.isBlank()) return emptyList()
        val all = _items.value + userListedItems.value.map { Item(it.name, it.category) }
        return all.filter { it.name.contains(query, ignoreCase = true) }
    }

    fun getGoingSoon() = _items.value.filter { it.name == "Bread" || it.name == "Milk" }

    // ── ViewModelFactory ──────────────────────────────────────────────
    // Required because ViewModel now has a constructor parameter (repository)
    class Factory(private val repository: ReServeRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReServeViewModel::class.java))
                return ReServeViewModel(repository) as T
            throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}