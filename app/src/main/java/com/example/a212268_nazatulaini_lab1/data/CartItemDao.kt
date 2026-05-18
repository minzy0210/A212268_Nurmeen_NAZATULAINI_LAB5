package com.example.a212268_nazatulaini_lab1.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// 2. DAO — defines all database operations for cart items
@Dao
interface CartItemDao {

    // Insert or update (e.g. quantity bump)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartItemEntity)

    // Observe cart as a live stream
    @Query("SELECT * FROM cart_items")
    fun getAll(): Flow<List<CartItemEntity>>

    // Remove one item by name
    @Query("DELETE FROM cart_items WHERE name = :name")
    suspend fun deleteByName(name: String)

    // Wipe entire cart (e.g. after checkout)
    @Query("DELETE FROM cart_items")
    suspend fun clearAll()

    // Update quantity of one cart item
    @Update
    suspend fun update(item: CartItemEntity)
}