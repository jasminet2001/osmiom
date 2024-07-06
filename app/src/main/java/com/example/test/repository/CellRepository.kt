package com.example.test.repository

import com.example.test.database.cellDao
import com.example.test.model.CellInfoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class CellRepository(private val cellInfoDao: cellDao) {
    // Function to get all cell info
    fun getAllCellInfo(): Flow<List<CellInfoEntity>> = flow {
        emit(cellInfoDao.getAllCellInfo())
    }

    suspend fun insert(cellInfoEntity: CellInfoEntity) {
        cellInfoDao.insert(cellInfoEntity)
    }
}