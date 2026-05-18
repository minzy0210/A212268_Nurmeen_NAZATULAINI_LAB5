package com.example.a212268_nazatulaini_lab1.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 3. ROOM DATABASE — singleton that ties all entities + DAOs together
@Database(
    entities = [
        UserListedItemEntity::class,
        CartItemEntity::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = false          // set true + add schema dir if you want migrations
)
abstract class ReServeDatabase : RoomDatabase() {

    // Room generates the implementations of these at compile time
    abstract fun userListedItemDao(): UserListedItemDao
    abstract fun cartItemDao(): CartItemDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        // @Volatile ensures every thread sees the same instance immediately
        @Volatile
        private var INSTANCE: ReServeDatabase? = null

        fun getInstance(context: Context): ReServeDatabase {
            // Double-checked locking — only one database is ever built
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ReServeDatabase::class.java,
                    "reserve_database"       // file name on disk
                )
                    .fallbackToDestructiveMigration() // wipe & rebuild on schema change (dev only)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}