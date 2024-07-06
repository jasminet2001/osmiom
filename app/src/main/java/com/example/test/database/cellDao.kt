package com.example.test.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.test.model.CellInfoEntity

@Dao
interface cellDao {
    @Insert
    suspend fun insert(cellInfoEntity: CellInfoEntity)

    @Query("SELECT * FROM cell_info")
    suspend fun getAllCellInfo(): List<CellInfoEntity>
}