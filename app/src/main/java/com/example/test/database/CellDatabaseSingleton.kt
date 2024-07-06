package com.example.test.database

import android.content.Context
import androidx.room.Room

object CellDatabaseSingleton {
    @Volatile
    private var INSTANCE: CellDB? = null

    fun getDatabase(context: Context): CellDB {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                CellDB::class.java,
                "cell_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
