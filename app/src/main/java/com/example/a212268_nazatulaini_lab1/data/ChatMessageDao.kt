package com.example.a212268_nazatulaini_lab1.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// 2. DAO — defines all database operations for chat messages
@Dao
interface ChatMessageDao {

    // Insert a new message
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: ChatMessageEntity)

    // Observe all messages for a specific conversation thread
    @Query("""
        SELECT * FROM chat_messages
        WHERE ownerName = :owner AND itemName = :item
        ORDER BY timestampMs ASC
    """)
    fun getMessagesForConversation(owner: String, item: String): Flow<List<ChatMessageEntity>>

    // Observe all unique conversation threads (for inbox screen)
    @Query("""
        SELECT * FROM chat_messages
        WHERE id IN (
            SELECT MAX(id) FROM chat_messages GROUP BY ownerName, itemName
        )
        ORDER BY timestampMs DESC
    """)
    fun getAllConversations(): Flow<List<ChatMessageEntity>>

    // Delete all messages in a conversation thread
    @Query("DELETE FROM chat_messages WHERE ownerName = :owner AND itemName = :item")
    suspend fun deleteConversation(owner: String, item: String)
}