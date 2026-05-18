package com.example.a212268_nazatulaini_lab1.data

import com.example.a212268_nazatulaini_lab1.CartItem
import com.example.a212268_nazatulaini_lab1.UserListedItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 4. REPOSITORY — single source of truth between ViewModel and DAOs
//    Keeps the ViewModel free of database details.
class ReServeRepository(
    private val userListedItemDao: UserListedItemDao,
    private val cartItemDao: CartItemDao,
    private val chatMessageDao: ChatMessageDao
) {

    // ── UserListedItem ────────────────────────────────────────────────

    // Expose as Flow of domain model (not entity) — ViewModel stays DB-agnostic
    val userListedItems: Flow<List<UserListedItem>> =
        userListedItemDao.getAll().map { list -> list.map { it.toDomain() } }

    suspend fun addUserItem(item: UserListedItem) =
        userListedItemDao.insert(item.toEntity())

    suspend fun deleteUserItem(item: UserListedItem) =
        userListedItemDao.delete(item.toEntity())

    suspend fun updateUserItem(item: UserListedItem) =
        userListedItemDao.update(item.toEntity())

    suspend fun getUserItemByName(name: String): UserListedItem? =
        userListedItemDao.getByName(name)?.toDomain()

    // ── CartItem ──────────────────────────────────────────────────────

    val cartItems: Flow<List<CartItem>> =
        cartItemDao.getAll().map { list -> list.map { it.toDomain() } }

    suspend fun addToCart(item: CartItem) =
        cartItemDao.insert(item.toEntity())

    suspend fun updateCartItem(item: CartItem) =
        cartItemDao.update(item.toEntity())

    suspend fun removeFromCart(name: String) =
        cartItemDao.deleteByName(name)

    suspend fun clearCart() =
        cartItemDao.clearAll()

    // ── ChatMessage ───────────────────────────────────────────────────

    fun getMessagesForConversation(owner: String, item: String): Flow<List<ChatMessageEntity>> =
        chatMessageDao.getMessagesForConversation(owner, item)

    fun getAllConversations(): Flow<List<ChatMessageEntity>> =
        chatMessageDao.getAllConversations()

    suspend fun sendMessage(message: ChatMessageEntity) =
        chatMessageDao.insert(message)

    suspend fun deleteConversation(owner: String, item: String) =
        chatMessageDao.deleteConversation(owner, item)
}

// ── Mappers: Entity ↔ Domain model ───────────────────────────────────
//   Kept here so neither the UI nor ViewModel ever imports Entity classes.

fun UserListedItemEntity.toDomain() = UserListedItem(
    name            = name,
    category        = category,
    photoUri        = photoUri,
    sellerName      = sellerName,
    location        = location,
    description     = description,
    quantity        = quantity,
    originalPrice   = originalPrice,
    discountPercent = discountPercent,
    expiresIn       = expiresIn,
    deposit         = deposit,
    maxBorrowDays   = maxBorrowDays,
    condition       = condition,
    availableUntil  = availableUntil
)

fun UserListedItem.toEntity() = UserListedItemEntity(
    name            = name,
    category        = category,
    photoUri        = photoUri,
    sellerName      = sellerName,
    location        = location,
    description     = description,
    quantity        = quantity,
    originalPrice   = originalPrice,
    discountPercent = discountPercent,
    expiresIn       = expiresIn,
    deposit         = deposit,
    maxBorrowDays   = maxBorrowDays,
    condition       = condition,
    availableUntil  = availableUntil
)

fun CartItemEntity.toDomain() = CartItem(
    name      = name,
    imageRes  = imageRes,
    price     = price,
    quantity  = quantity
)

fun CartItem.toEntity() = CartItemEntity(
    name      = name,
    imageRes  = imageRes,
    price     = price,
    quantity  = quantity
)