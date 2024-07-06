package com.example.test.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.test.model.CellInfoEntity

@Database(entities = [CellInfoEntity::class], version = 1)
abstract class CellDB : RoomDatabase() {
    abstract fun cellInfoDao(): cellDao
}