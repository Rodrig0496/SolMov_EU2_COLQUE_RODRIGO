package com.example.myapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
import com.example.myapplication.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE deleted = 0 ORDER BY id DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id AND deleted = 0")
    fun getProductById(id: Int): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductByIdOneShot(id: Int): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity)

    @Update
    suspend fun update(product: ProductEntity)

    @Delete
    suspend fun delete(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM products WHERE pendingSync = 1")
    suspend fun pendingProducts(): List<ProductEntity>

    @Query("SELECT COALESCE(MIN(id), -1) - 1 FROM products WHERE id < 0")
    suspend fun nextLocalId(): Int

    @Query("UPDATE products SET pendingSync = 0 WHERE id = :id AND title = :title AND price = :price AND description = :description AND category = :category AND image = :image AND deleted = :deleted")
    suspend fun markSynced(id: Int, title: String, price: Double, description: String, category: String, image: String, deleted: Boolean)

    @Transaction
    suspend fun mergeRemote(products: List<ProductEntity>) {
        products.forEach { remote ->
            if (getProductByIdOneShot(remote.id)?.locallyModified != true) insert(remote)
        }
    }

    @Transaction
    suspend fun createLocal(product: ProductEntity) {
        insert(product.copy(id = nextLocalId(), locallyModified = true, pendingSync = true))
    }

    @Query("UPDATE products SET deleted = 1, locallyModified = 1, pendingSync = 1 WHERE id = :id")
    suspend fun markDeleted(id: Int)

    @Query("DELETE FROM products")
    suspend fun deleteAll()
}
