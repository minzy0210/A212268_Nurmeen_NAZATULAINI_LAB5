package com.example.a212268_nazatulaini_lab1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// 1. ENTITY — maps to the "chat_messages" table in Room
@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val ownerName: String,      // conversation thread key
    val itemName: String,       // conversation thread key
    val text: String,
    val isFromMe: Boolean,
    val timestampMs: Long = System.currentTimeMillis()
)