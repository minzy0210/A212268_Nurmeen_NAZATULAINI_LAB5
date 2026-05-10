package com.example.a212268_nazatulaini_lab1

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ReServeViewModel : ViewModel() {

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

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _reservedQuantities = MutableStateFlow<Map<String, Int>>(emptyMap())
    val reservedQuantities: StateFlow<Map<String, Int>> = _reservedQuantities.asStateFlow()

    private val _borrowedItems = MutableStateFlow<Set<String>>(emptySet())
    val borrowedItems: StateFlow<Set<String>> = _borrowedItems.asStateFlow()

    // ── Replaced Triple with UserListedItem ──────────────────────────
    private val _userListedItems = MutableStateFlow<List<UserListedItem>>(emptyList())
    val userListedItems: StateFlow<List<UserListedItem>> = _userListedItems.asStateFlow()

    fun addUserItem(item: UserListedItem) {
        _userListedItems.value = _userListedItems.value + item
    }

    fun getUserListedItem(name: String): UserListedItem? =
        _userListedItems.value.firstOrNull { it.name == name }

    fun getPhotoUri(name: String): String? =
        _userListedItems.value.firstOrNull { it.name == name }?.photoUri

    fun getDistance(name: String): String {
        // Check user listed items first
        _userListedItems.value.firstOrNull { it.name == name }?.let { return it.location }
        // Fall back to hardcoded items
        return when (name) {
            "Apple" -> "1.2km"; "Bread" -> "0.8km"; "Milk" -> "2.1km"
            "Cake" -> "3.5km"; "Banana" -> "0.5km"; "Pizza" -> "1.9km"
            "Guitar" -> "2.3km"; "Trampoline" -> "4.1km"; "Plant Pot" -> "0.9km"
            "Chair" -> "1.5km"; "Table" -> "3.2km"; "Books" -> "1.1km"
            else -> "N/A"
        }
    }

    fun addToCart(itemName: String, quantity: Int = 1) {
        val foodItem = getFoodItemData(itemName)
        val discountedPrice = foodItem.originalPrice * (1 - foodItem.discountPercent / 100.0)
        val imageRes = getItemImage(itemName)
        _cartItems.update { currentCart ->
            val existing = currentCart.find { it.name == itemName }
            if (existing != null) {
                currentCart.map {
                    if (it.name == itemName) it.copy(quantity = it.quantity + quantity) else it
                }
            } else {
                currentCart + CartItem(name = itemName, imageRes = imageRes, price = discountedPrice, quantity = quantity)
            }
        }
    }

    fun removeFromCart(itemName: String) {
        _cartItems.update { it.filter { c -> c.name != itemName } }
    }

    fun clearCart() { _cartItems.value = emptyList() }

    fun getCartTotal(): Double = _cartItems.value.sumOf { it.price * it.quantity }

    fun reserveFoodItem(itemName: String, quantity: Int) {
        _reservedQuantities.update { current ->
            current.toMutableMap().also { it[itemName] = (it[itemName] ?: 0) + quantity }
        }
    }

    fun borrowNonFoodItem(itemName: String) {
        _borrowedItems.update { it + itemName }
    }

    fun getRemainingStock(itemName: String): Int {
        // For user listed food items, use their quantity field
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

    // Change getFoodItems():
    fun getFoodItems(): List<Item> {
        val base = _items.value.filter { it.category == "Food" }
        val userFood = _userListedItems.value
            .filter { it.category.equals("Food", ignoreCase = true) }
            .map { Item(it.name, "Food") }
        return base + userFood
    }

    // Change getNonFoodItems():
    fun getNonFoodItems(): List<Item> {
        val base = _items.value.filter { it.category == "Non-food" }
        val userNonFood = _userListedItems.value
            .filter { it.category.equals("Non-food", ignoreCase = true) ||
                    it.category.equals("Non-Food", ignoreCase = true) }
            .map { Item(it.name, "Non-food") }
        return base + userNonFood
    }

    // Change searchItems():
    fun searchItems(query: String): List<Item> {
        if (query.isBlank()) return emptyList()
        val all = _items.value + _userListedItems.value.map {
            Item(it.name, it.category)
        }
        return all.filter { it.name.contains(query, ignoreCase = true) }
    }

    fun getGoingSoon() = _items.value.filter { it.name == "Bread" || it.name == "Milk" }


    fun decrementCart(itemName: String) {
        _cartItems.update { currentCart ->
            val existing = currentCart.find { it.name == itemName }
            if (existing != null && existing.quantity > 1) {
                currentCart.map { if (it.name == itemName) it.copy(quantity = it.quantity - 1) else it }
            } else {
                currentCart.filter { it.name != itemName }
            }
        }
    }
}