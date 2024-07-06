package com.example.test.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cell_info")
data class CellInfoEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cellId: Int,
    val cellLocationX: Double,
    val cellLocationY: Double,
    val signalStrength: Int
)