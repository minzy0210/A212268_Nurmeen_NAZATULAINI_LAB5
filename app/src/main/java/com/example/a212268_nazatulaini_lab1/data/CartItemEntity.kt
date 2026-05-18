package com.example.a212268_nazatulaini_lab1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// 1. ENTITY — maps to the "cart_items" table in Room
@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey
    val name: String,           // item name is unique in cart
    val imageRes: Int,
    val price: Double,
    val quantity: Int = 1
)