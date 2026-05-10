package com.example.a212268_nazatulaini_lab1

data class Item(
    val name: String,
    val category: String
)

data class CartItem(
    val name: String,
    val imageRes: Int,
    val price: Double,
    val quantity: Int = 1
)

data class UserListedItem(
    val name: String,
    val category: String,
    val photoUri: String?,
    val sellerName: String,
    val location: String,
    // Food fields
    val quantity: Int = 1,
    val originalPrice: Double = 0.0,
    val discountPercent: Int = 0,
    val expiresIn: String = "Ongoing",
    val description: String = "",
    // Non-food fields
    val deposit: Double = 0.0,
    val maxBorrowDays: Int = 7,
    val condition: String = "Good",
    val availableUntil: String = "Ongoing"
)