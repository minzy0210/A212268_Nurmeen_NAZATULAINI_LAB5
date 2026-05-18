package com.example.a212268_nazatulaini_lab1

import android.app.Application
import com.example.a212268_nazatulaini_lab1.data.ReServeDatabase
import com.example.a212268_nazatulaini_lab1.data.ReServeRepository

// Application subclass — created once when the app starts.
// Provides the database + repository as singletons accessible anywhere.
class ReServeApplication : Application() {

    // Lazy: only built the first time .database is accessed
    val database by lazy { ReServeDatabase.getInstance(this) }

    val repository by lazy {
        ReServeRepository(
            userListedItemDao = database.userListedItemDao(),
            cartItemDao       = database.cartItemDao(),
            chatMessageDao    = database.chatMessageDao()
        )
    }
}