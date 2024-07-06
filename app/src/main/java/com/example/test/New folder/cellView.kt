package com.example.test.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.test.model.CellInfoEntity
import com.example.test.repository.CellRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class cellView(private val repository: CellRepository) : ViewModel() {
    // Function to get all cell info
    fun getAllCellInfo(): Flow<List<CellInfoEntity>> {
        return repository.getAllCellInfo()
    }

    // Function to insert cell info
    fun insert(cellInfoEntity: CellInfoEntity) = viewModelScope.launch {
        repository.insert(cellInfoEntity)
    }
}

class cellInfoViewFactory(private val repository: CellRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(cellView::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return cellView(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}