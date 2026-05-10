package com.example.a212268_nazatulaini_lab1

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ChatMessage(
    val id: String,
    val text: String,
    val isFromMe: Boolean,
    val timestamp: String
)

data class Conversation(
    val ownerName: String,
    val itemName: String,
    val itemImageRes: Int,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int = 0
)

class ChatViewModel : ViewModel() {
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _messages = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())
    val messages: StateFlow<Map<String, List<ChatMessage>>> = _messages.asStateFlow()

    fun startConversation(ownerName: String, itemName: String, itemImageRes: Int) {
        val existing = _conversations.value.any { it.ownerName == ownerName && it.itemName == itemName }
        if (!existing) {
            val greeting = ChatMessage(
                id = System.currentTimeMillis().toString(),
                text = "Hi! I'm interested in your $itemName.",
                isFromMe = true,
                timestamp = "Now"
            )
            val reply = ChatMessage(
                id = (System.currentTimeMillis() + 1).toString(),
                text = "Hello! Sure, feel free to ask me anything about it.",
                isFromMe = false,
                timestamp = "Now"
            )
            val key = conversationKey(ownerName, itemName)
            _messages.value = _messages.value + (key to listOf(greeting, reply))
            _conversations.value = _conversations.value + Conversation(
                ownerName = ownerName,
                itemName = itemName,
                itemImageRes = itemImageRes,
                lastMessage = reply.text,
                timestamp = "Now",
                unreadCount = 1
            )
        }
    }

    fun sendMessage(ownerName: String, itemName: String, text: String) {
        if (text.isBlank()) return
        val key = conversationKey(ownerName, itemName)
        val newMsg = ChatMessage(
            id = System.currentTimeMillis().toString(),
            text = text,
            isFromMe = true,
            timestamp = "Now"
        )
        val current = _messages.value[key] ?: emptyList()
        _messages.value = _messages.value + (key to (current + newMsg))

        // Update last message in conversation list
        _conversations.value = _conversations.value.map {
            if (it.ownerName == ownerName && it.itemName == itemName)
                it.copy(lastMessage = text, timestamp = "Now")
            else it
        }
    }

    fun getMessages(ownerName: String, itemName: String): List<ChatMessage> {
        return _messages.value[conversationKey(ownerName, itemName)] ?: emptyList()
    }

    fun markAsRead(ownerName: String, itemName: String) {
        _conversations.value = _conversations.value.map {
            if (it.ownerName == ownerName && it.itemName == itemName) it.copy(unreadCount = 0)
            else it
        }
    }

    private fun conversationKey(ownerName: String, itemName: String) = "$ownerName::$itemName"
}