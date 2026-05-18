package com.example.a212268_nazatulaini_lab1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// 1. ENTITY — maps to the "user_listed_items" table in Room
@Entity(tableName = "user_listed_items")
data class UserListedItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Basic
    val name: String,
    val category: String,
    val photoUri: String?,
    val sellerName: String,
    val location: String,
    val description: String = "",

    // Food-specific
    val quantity: Int = 1,
    val originalPrice: Double = 0.0,
    val discountPercent: Int = 0,
    val expiresIn: String = "Ongoing",

    // Non-food-specific
    val deposit: Double = 0.0,
    val maxBorrowDays: Int = 7,
    val condition: String = "Good",
    val availableUntil: String = "Ongoing"
)