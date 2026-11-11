package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<List<Product>>
    fun getProductById(id: Int): Flow<Product?>
    suspend fun refreshProducts(): Result<Unit>
    suspend fun addProduct(product: Product): Result<Unit>
    suspend fun updateProduct(product: Product): Result<Unit>
    suspend fun deleteProduct(id: Int): Result<Unit>
}
