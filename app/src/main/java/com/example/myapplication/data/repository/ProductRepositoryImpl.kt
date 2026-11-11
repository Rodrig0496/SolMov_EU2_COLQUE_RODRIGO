package com.example.myapplication.data.repository

import com.example.myapplication.data.local.dao.ProductDao
import com.example.myapplication.data.remote.api.FakeStoreApiService
import com.example.myapplication.data.remote.dto.toEntity
import com.example.myapplication.domain.model.Product
import com.example.myapplication.domain.model.toDomain
import com.example.myapplication.domain.model.toDto
import com.example.myapplication.domain.model.toEntity
import com.example.myapplication.domain.repository.ProductRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.map

class ProductRepositoryImpl(
    private val productDao: ProductDao,
    private val apiService: FakeStoreApiService,
    private val scheduleSync: () -> Unit = {}
) : ProductRepository {
    override fun getProducts() = productDao.getAllProducts().map { rows -> rows.map { it.toDomain() } }
    override fun getProductById(id: Int) = productDao.getProductById(id).map { it?.toDomain() }

    private suspend fun localResult(action: suspend () -> Unit): Result<Unit> = try {
        action()
        Result.success(Unit)
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (error: Exception) {
        Result.failure(error)
    }

    override suspend fun refreshProducts(): Result<Unit> = localResult {
        productDao.mergeRemote(apiService.getProducts().map { it.toEntity() })
    }

    override suspend fun addProduct(product: Product): Result<Unit> = localResult {
        productDao.createLocal(product.toEntity())
        scheduleSync()
    }

    override suspend fun updateProduct(product: Product): Result<Unit> = localResult {
        productDao.update(product.toEntity().copy(locallyModified = true, pendingSync = true))
        scheduleSync()
    }

    override suspend fun deleteProduct(id: Int): Result<Unit> = localResult {
        productDao.markDeleted(id)
        scheduleSync()
    }

    // FakeStore simulates writes; preserve local overrides even after a successful response.
    // Otherwise the next GET would undo edits and resurrect deleted products.
    suspend fun syncPending(): Result<Unit> = localResult {
        productDao.pendingProducts().forEach { row ->
            when {
                row.deleted && row.id > 0 -> apiService.deleteProduct(row.id)
                row.deleted -> Unit
                row.id < 0 -> apiService.addProduct(row.toDomain().toDto().copy(id = null))
                else -> apiService.updateProduct(row.id, row.toDomain().toDto())
            }
            productDao.markSynced(row.id, row.title, row.price, row.description, row.category, row.image, row.deleted)
        }
    }
}
